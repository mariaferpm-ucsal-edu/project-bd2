"""Encontra pares de produtos frequentes e gera regras de associacao."""

import argparse
import hashlib
import io
import json
from datetime import datetime, timezone
from importlib.metadata import version
from pathlib import Path

import pandas as pd
from mlxtend.frequent_patterns import apriori, association_rules

from preparar_dados import preparar_compras


PASTA = Path(__file__).resolve().parent


def minerar(matriz, suporte=0.02, confianca=0.60, lift=1.0):
    """Usa todas as compras como denominador, inclusive as unitarias."""
    if not 0 < suporte <= 1:
        raise ValueError("O suporte deve ser maior que 0 e menor ou igual a 1.")
    if not 0 <= confianca <= 1:
        raise ValueError("A confianca deve estar entre 0 e 1.")
    if not 0 <= lift < float("inf"):
        raise ValueError("O limite de lift deve ser finito e maior ou igual a 0.")
    if matriz.empty:
        raise ValueError("Nao ha cestas para analisar.")

    # Comecamos com pares: cada regra relaciona um produto a outro.
    conjuntos = apriori(matriz, min_support=suporte, use_colnames=True, max_len=2)
    if conjuntos.empty or not conjuntos["itemsets"].map(len).ge(2).any():
        regras = pd.DataFrame(columns=["antecedents", "consequents", "support", "confidence", "lift"])
    else:
        regras = association_rules(
            conjuntos,
            num_itemsets=len(matriz),
            metric="confidence",
            min_threshold=confianca,
        )
        regras = regras.loc[regras["lift"] > lift].copy()
        regras = regras.sort_values(
            ["lift", "confidence", "support"], ascending=False
        ).reset_index(drop=True)
    return conjuntos, regras


def formatar_resultados(conjuntos, regras, produtos, total_compras):
    nomes = dict(zip(produtos["codigo_produto"], produtos["produto"]))

    def codigos(itens):
        return json.dumps(sorted(itens), ensure_ascii=False)

    def rotulos(itens):
        return "; ".join(f"{nomes[codigo]} [codigo {codigo}]" for codigo in sorted(itens))

    frequentes = pd.DataFrame([
        {
            "codigos_produtos": codigos(linha.itemsets),
            "produtos": rotulos(linha.itemsets),
            "suporte": linha.support,
            "compras_com_a_combinacao": int(round(linha.support * total_compras)),
        }
        for linha in conjuntos.itertuples(index=False)
    ], columns=["codigos_produtos", "produtos", "suporte", "compras_com_a_combinacao"])

    associacoes = pd.DataFrame([
        {
            "antecedente_codigos": codigos(linha.antecedents),
            "antecedente_produtos": rotulos(linha.antecedents),
            "consequente_codigos": codigos(linha.consequents),
            "consequente_produtos": rotulos(linha.consequents),
            "suporte": linha.support,
            "confianca": linha.confidence,
            "lift": linha.lift,
            "compras_com_a_combinacao": int(round(linha.support * total_compras)),
        }
        for linha in regras.itertuples(index=False)
    ], columns=[
        "antecedente_codigos", "antecedente_produtos",
        "consequente_codigos", "consequente_produtos",
        "suporte", "confianca", "lift", "compras_com_a_combinacao",
    ])
    return frequentes, associacoes


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--base", type=Path, default=PASTA / "dados_preparados" / "compras.csv")
    parser.add_argument("--saida", type=Path, default=PASTA / "resultados_apriori")
    parser.add_argument("--suporte", type=float, default=0.02)
    parser.add_argument("--confianca", type=float, default=0.60)
    parser.add_argument("--lift", type=float, default=1.0, help="Seleciona regras com lift estritamente maior que este limite.")
    args = parser.parse_args()

    if not args.base.is_file():
        print("A base de compras ainda nao foi gerada.")
        print("Depois do ETL, execute preparar_dados.py e rode este script novamente.")
        print("Nenhum resultado foi criado ou atualizado nesta execucao.")
        return

    conteudo = args.base.read_bytes()
    dados = pd.read_csv(io.BytesIO(conteudo), dtype={
        "codigo_venda": "string", "codigo_produto": "string", "produto": "string",
    }, encoding="utf-8-sig", keep_default_na=False)
    if dados.empty:
        print("A base esta vazia. Carregue os dados pelo ETL e refaca a preparacao.")
        print("Nenhum resultado foi criado ou atualizado nesta execucao.")
        return

    compras, produtos, matriz = preparar_compras(dados)
    conjuntos, regras = minerar(matriz, args.suporte, args.confianca, args.lift)
    frequentes, associacoes = formatar_resultados(conjuntos, regras, produtos, len(matriz))

    resumo = {
        "executado_em_utc": datetime.now(timezone.utc).isoformat(),
        "algoritmo": "Apriori e regras de associacao",
        "suporte_minimo": args.suporte,
        "confianca_minima": args.confianca,
        "lift_estritamente_maior_que": args.lift,
        "tamanho_maximo_dos_conjuntos": 2,
        "compras": len(matriz),
        "produtos": len(matriz.columns),
        "compras_unitarias_incluidas": int(matriz.sum(axis=1).eq(1).sum()),
        "compras_com_mais_de_um_produto": int(matriz.sum(axis=1).gt(1).sum()),
        "conjuntos_frequentes": len(conjuntos),
        "regras_selecionadas": len(regras),
        "sha256_arquivo_entrada": hashlib.sha256(conteudo).hexdigest(),
        "versoes": {nome: version(nome) for nome in ["pandas", "mlxtend", "numpy", "scikit-learn"]},
    }

    args.saida.mkdir(parents=True, exist_ok=True)
    (args.saida / "base_utilizada.csv").write_bytes(conteudo)
    produtos.to_csv(args.saida / "produtos.csv", index=False, encoding="utf-8-sig")
    matriz.to_csv(args.saida / "cestas.csv", encoding="utf-8-sig")
    frequentes.to_csv(args.saida / "combinacoes_frequentes.csv", index=False, encoding="utf-8-sig")
    associacoes.to_csv(args.saida / "regras_de_associacao.csv", index=False, encoding="utf-8-sig")
    (args.saida / "parametros_e_resumo.json").write_text(
        json.dumps(resumo, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    print(f"Compras analisadas: {len(matriz)}")
    print(f"Produtos distintos: {len(matriz.columns)}")
    print(f"Conjuntos frequentes: {len(conjuntos)}")
    print(f"Regras selecionadas: {len(regras)}")
    if associacoes.empty:
        print("Nenhuma regra atendeu aos criterios. Esse resultado deve ser registrado.")
    else:
        print(associacoes.to_string(index=False))
    print(f"Arquivos salvos em: {args.saida.resolve()}")
    print("As conclusoes de negocio devem ser elaboradas a partir destes resultados e suas limitacoes.")


if __name__ == "__main__":
    main()
