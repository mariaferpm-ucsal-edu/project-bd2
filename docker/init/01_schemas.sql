-- Executado automaticamente na primeira subida do container.
CREATE SCHEMA IF NOT EXISTS oltp;     -- sistema transacional (Hibernate)
CREATE SCHEMA IF NOT EXISTS staging;  -- area de staging do ETL
CREATE SCHEMA IF NOT EXISTS dw;       -- modelo estrela
CREATE SCHEMA IF NOT EXISTS dw_floco; -- modelo floco de neve
