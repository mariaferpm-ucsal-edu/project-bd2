"""Prepara as compras do DW para a mineracao com Apriori."""

import json
import os
from getpass import getpass
from pathlib import Path

import pandas as pd
from mlxtend.preprocessing import TransactionEncoder
from sqlalchemy import URL, create_engine, text


PASTA = Path(__file__).resolve().parent


def preparar_compras(dados):
    """Retorna pares venda/produto, catalogo e matriz booleana de cestas."""
    campos = ["codigo_venda", "codigo_produto", "produto"]
    if not set(campos).issubset(dados.columns):
        raise ValueError("A consulta precisa retornar codigo_venda, codigo_produto e produto.")
    if dados.empty:
        raise ValueError("Nao ha compras para preparar.")

    compras = dados[campos].copy()
    if compras.isna().any().any():
        raise ValueError("Existem identificadores ou nomes ausentes. Confira a carga do DW.")

    # Os codigos identificam os produtos; nomes iguais nao unem produtos distintos.
    for campo in ["codigo_venda", "codigo_produto"]:
        compras[campo] = compras[campo].astype(str).str.strip()
        if compras[campo].eq("").any():
            raise ValueError(f"Existem valores vazios em {campo}.")

    produtos = compras[["codigo_produto", "produto"]].drop_duplicates()
    if produtos["codigo_produto"].duplicated().any():
        raise ValueError("Um mesmo codigo de produto possui nomes diferentes na extracao.")
    produtos = produtos.sort_values("codigo_produto").reset_index(drop=True)

    # Uma ocorrencia por produto em cada compra, independentemente da quantidade.
    compras = compras.drop_duplicates(["codigo_venda", "codigo_produto"])
    compras = compras.sort_values(["codigo_venda", "codigo_produto"]).reset_index(drop=True)
    cestas = compras.groupby("codigo_venda", sort=True)["codigo_produto"].agg(list)

    # Todas as compras sao mantidas, inclusive as que possuem somente um produto.
    encoder = TransactionEncoder()
    presencas = encoder.fit(cestas.tolist()).transform(cestas.tolist())
    matriz = pd.DataFrame(presencas, index=cestas.index, columns=encoder.columns_)
    matriz.index.name = "codigo_venda"
    return compras, produtos, matriz


def main():
    arquivo_sql = PASTA / "compras_para_mineracao.sql"
    consulta = text(arquivo_sql.read_text(encoding="utf-8-sig"))

    usuario = os.getenv("DB_USER", "postgres")
    senha = getpass(f"Senha do PostgreSQL para o usuario {usuario}: ")
    url = URL.create(
        "postgresql+psycopg2",
        username=usuario,
        password=senha,
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "5432")),
        database=os.getenv("DB_NAME", "vendas"),
    )
    engine = create_engine(url, connect_args={"connect_timeout": 10})
    try:
        with engine.connect() as conexao:
            dados = pd.read_sql_query(consulta, conexao)
    finally:
        engine.dispose()

    if dados.empty:
        print("\nO DW ainda esta sem compras.")
        print("Execute novamente depois da carga pelo ETL.")
        print("Nenhum arquivo de dados foi criado ou atualizado nesta execucao.")
        return

    compras, produtos, matriz = preparar_compras(dados)
    resumo = {
        "linhas_extraidas": len(dados),
        "pares_venda_produto": len(compras),
        "compras": len(matriz),
        "produtos": len(matriz.columns),
        "compras_com_um_produto": int(matriz.sum(axis=1).eq(1).sum()),
        "compras_com_mais_de_um_produto": int(matriz.sum(axis=1).gt(1).sum()),
    }

    saida = PASTA / "dados_preparados"
    saida.mkdir(parents=True, exist_ok=True)
    compras.to_csv(saida / "compras.csv", index=False, encoding="utf-8-sig")
    produtos.to_csv(saida / "produtos.csv", index=False, encoding="utf-8-sig")
    matriz.to_csv(saida / "cestas.csv", encoding="utf-8-sig")
    (saida / "resumo.json").write_text(
        json.dumps(resumo, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    print("\nDados preparados com sucesso!")
    for nome, valor in resumo.items():
        print(f"{nome}: {valor}")
    print(f"Arquivos salvos em: {saida}")


if __name__ == "__main__":
    main()
