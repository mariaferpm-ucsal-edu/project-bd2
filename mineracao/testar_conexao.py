from getpass import getpass

import pandas as pd
from sqlalchemy import URL, create_engine, text

senha = getpass("Senha do PostgreSQL para o usuario postgres: ")

url = URL.create(
    "postgresql+psycopg2",
    username="postgres",
    password=senha,
    host="localhost",
    port=5432,
    database="vendas",
)

engine = create_engine(
    url,
    connect_args={"connect_timeout": 10},
)

consulta = text("""
    SELECT
        current_database() AS banco,
        current_user AS usuario,
        (SELECT COUNT(*) FROM dw.fato_venda) AS itens_estrela,
        (SELECT COUNT(*) FROM dw_floco.fato_venda) AS itens_floco
""")

try:
    with engine.connect() as conexao:
        resultado = pd.read_sql_query(consulta, conexao)

    print("\nConexao funcionando!")
    print(resultado.to_string(index=False))
finally:
    engine.dispose()