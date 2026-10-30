"""Conexao com o Data Warehouse (PostgreSQL) para a etapa de mineracao."""
import os

import pandas as pd
from dotenv import load_dotenv
from sqlalchemy import create_engine

load_dotenv()


def get_engine():
    url = "postgresql+psycopg2://{u}:{p}@{h}:{port}/{d}".format(
        u=os.getenv("DB_USER", "bd2"),
        p=os.getenv("DB_PASSWORD", "bd2"),
        h=os.getenv("DB_HOST", "localhost"),
        port=os.getenv("DB_PORT", "5432"),
        d=os.getenv("DB_NAME", "vendas"),
    )
    return create_engine(url)


def query(sql: str) -> pd.DataFrame:
    return pd.read_sql(sql, get_engine())


if __name__ == "__main__":
    print(query("select version() as versao"))
