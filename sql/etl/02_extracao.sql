
-- ============================================================
-- ETL - REDE DE FARMACIAS
-- 02 - EXTRACAO DOS DADOS DO OLTP PARA O STAGING
-- ============================================================

BEGIN;

-- Limpa os dados da extracao anterior
TRUNCATE TABLE
    staging.stg_item_venda,
    staging.stg_venda,
    staging.stg_produto,
    staging.stg_categoria,
    staging.stg_laboratorio,
    staging.stg_cliente,
    staging.stg_loja;

-- CLIENTES
INSERT INTO staging.stg_cliente (
    id_origem, nome, cidade, uf, atualizado_em
)
SELECT
    c.id,
    p.nome,
    p.cidade,
    p.uf,
    p.atualizado_em
FROM oltp.cliente c
JOIN oltp.pessoa p ON p.id = c.id;

-- LOJAS
INSERT INTO staging.stg_loja (
    id_origem, nome, cidade, uf
)
SELECT
    id, nome, cidade, uf
FROM oltp.loja;

-- CATEGORIAS
INSERT INTO staging.stg_categoria (
    id_origem, nome
)
SELECT
    id, nome
FROM oltp.categoria;

-- LABORATORIOS
INSERT INTO staging.stg_laboratorio (
    id_origem, nome
)
SELECT
    id, nome
FROM oltp.laboratorio;

-- PRODUTOS
INSERT INTO staging.stg_produto (
    id_origem, nome, apresentacao,
    categoria_id_origem, categoria_nome,
    laboratorio_id_origem, laboratorio_nome
)
SELECT
    p.id,
    p.nome,
    p.apresentacao,
    c.id,
    c.nome,
    l.id,
    l.nome
FROM oltp.produto p
JOIN oltp.categoria c ON c.id = p.categoria_id
JOIN oltp.laboratorio l ON l.id = p.laboratorio_id;

-- VENDAS: todos os status sao extraidos
INSERT INTO staging.stg_venda (
    id_origem, cliente_id_origem, loja_id_origem,
    data_hora, status, atualizado_em
)
SELECT
    id,
    cliente_id,
    loja_id,
    data_hora,
    status,
    atualizado_em
FROM oltp.venda;

-- ITENS DE VENDA
INSERT INTO staging.stg_item_venda (
    id_origem, venda_id_origem, produto_id_origem,
    quantidade, preco_unitario, desconto_valor
)
SELECT
    id,
    venda_id,
    produto_id,
    quantidade,
    preco_unitario,
    desconto_valor
FROM oltp.item_venda;

COMMIT;
