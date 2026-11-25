
-- ============================================================
-- ETL - REDE DE FARMACIAS
-- 01 - ESTRUTURA DA CAMADA STAGING
-- ============================================================

CREATE SCHEMA IF NOT EXISTS staging;

-- CLIENTES
CREATE TABLE IF NOT EXISTS staging.stg_cliente (
    id_origem BIGINT PRIMARY KEY,
    nome VARCHAR(120),
    cidade VARCHAR(80),
    uf CHAR(2),
    atualizado_em TIMESTAMP
);

-- LOJAS
CREATE TABLE IF NOT EXISTS staging.stg_loja (
    id_origem BIGINT PRIMARY KEY,
    nome VARCHAR(120),
    cidade VARCHAR(80),
    uf CHAR(2)
);

-- CATEGORIAS
CREATE TABLE IF NOT EXISTS staging.stg_categoria (
    id_origem BIGINT PRIMARY KEY,
    nome VARCHAR(80)
);

-- LABORATORIOS
CREATE TABLE IF NOT EXISTS staging.stg_laboratorio (
    id_origem BIGINT PRIMARY KEY,
    nome VARCHAR(120)
);

-- PRODUTOS
CREATE TABLE IF NOT EXISTS staging.stg_produto (
    id_origem BIGINT PRIMARY KEY,
    nome VARCHAR(120),
    apresentacao VARCHAR(120),
    categoria_id_origem BIGINT,
    categoria_nome VARCHAR(80),
    laboratorio_id_origem BIGINT,
    laboratorio_nome VARCHAR(120)
);

-- VENDAS
CREATE TABLE IF NOT EXISTS staging.stg_venda (
    id_origem BIGINT PRIMARY KEY,
    cliente_id_origem BIGINT,
    loja_id_origem BIGINT,
    data_hora TIMESTAMP,
    status VARCHAR(20),
    atualizado_em TIMESTAMP
);

-- ITENS DE VENDA
CREATE TABLE IF NOT EXISTS staging.stg_item_venda (
    id_origem BIGINT PRIMARY KEY,
    venda_id_origem BIGINT,
    produto_id_origem BIGINT,
    quantidade INTEGER,
    preco_unitario NUMERIC(12,2),
    desconto_valor NUMERIC(12,2)
);
