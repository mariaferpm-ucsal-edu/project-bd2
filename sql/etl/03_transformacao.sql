
-- ============================================================
-- ETL - REDE DE FARMACIAS
-- 03 - TRANSFORMACAO DOS DADOS DO STAGING
-- ============================================================

BEGIN;

-- ============================================================
-- 1. PADRONIZACAO DE CLIENTES
-- ============================================================

UPDATE staging.stg_cliente
SET
    nome = COALESCE(NULLIF(BTRIM(nome), ''), 'Nao informado'),
    cidade = NULLIF(BTRIM(cidade), ''),
    uf = NULLIF(UPPER(BTRIM(uf)), '');

-- ============================================================
-- 2. PADRONIZACAO DE LOJAS
-- ============================================================

UPDATE staging.stg_loja
SET
    nome = COALESCE(NULLIF(BTRIM(nome), ''), 'Nao informado'),
    cidade = NULLIF(BTRIM(cidade), ''),
    uf = NULLIF(UPPER(BTRIM(uf)), '');

-- ============================================================
-- 3. PADRONIZACAO DE CATEGORIAS
-- ============================================================

UPDATE staging.stg_categoria
SET nome = COALESCE(NULLIF(BTRIM(nome), ''), 'Nao informado');

-- ============================================================
-- 4. PADRONIZACAO DE LABORATORIOS
-- ============================================================

UPDATE staging.stg_laboratorio
SET nome = COALESCE(NULLIF(BTRIM(nome), ''), 'Nao informado');

-- ============================================================
-- 5. PADRONIZACAO DE PRODUTOS
-- ============================================================

UPDATE staging.stg_produto
SET
    nome = COALESCE(NULLIF(BTRIM(nome), ''), 'Nao informado'),
    apresentacao = NULLIF(BTRIM(apresentacao), '');

-- Sincroniza os nomes com as dimensoes de categoria e laboratorio
UPDATE staging.stg_produto p
SET
    categoria_nome = c.nome
FROM staging.stg_categoria c
WHERE p.categoria_id_origem = c.id_origem;

UPDATE staging.stg_produto p
SET
    laboratorio_nome = l.nome
FROM staging.stg_laboratorio l
WHERE p.laboratorio_id_origem = l.id_origem;

-- ============================================================
-- 6. PADRONIZACAO DE VENDAS
-- ============================================================

UPDATE staging.stg_venda
SET status = UPPER(BTRIM(status));

-- ============================================================
-- 7. TABELA TRANSFORMADA DA FATO
-- Uma linha por item de venda
-- ============================================================

CREATE TABLE IF NOT EXISTS staging.stg_fato_venda (
    item_venda_id_origem BIGINT PRIMARY KEY,
    venda_id_origem BIGINT NOT NULL,
    cliente_id_origem BIGINT NOT NULL,
    loja_id_origem BIGINT NOT NULL,
    produto_id_origem BIGINT NOT NULL,
    data_venda DATE NOT NULL,
    status_venda VARCHAR(20) NOT NULL,
    quantidade INTEGER NOT NULL,
    valor_bruto NUMERIC(14,2) NOT NULL,
    desconto_valor NUMERIC(14,2) NOT NULL,
    valor_liquido NUMERIC(14,2) NOT NULL
);

-- Limpa somente o resultado da transformacao anterior
TRUNCATE TABLE staging.stg_fato_venda;

-- ============================================================
-- 8. CALCULO DAS MEDIDAS E ENRIQUECIMENTO DOS ITENS
-- ============================================================

INSERT INTO staging.stg_fato_venda (
    item_venda_id_origem,
    venda_id_origem,
    cliente_id_origem,
    loja_id_origem,
    produto_id_origem,
    data_venda,
    status_venda,
    quantidade,
    valor_bruto,
    desconto_valor,
    valor_liquido
)
SELECT
    i.id_origem,
    v.id_origem,
    v.cliente_id_origem,
    v.loja_id_origem,
    i.produto_id_origem,
    v.data_hora::DATE,
    v.status,
    i.quantidade,
    ROUND(i.preco_unitario * i.quantidade, 2),
    i.desconto_valor,
    ROUND(
        (i.preco_unitario * i.quantidade) - i.desconto_valor,
        2
    )
FROM staging.stg_item_venda i
JOIN staging.stg_venda v
    ON v.id_origem = i.venda_id_origem;

COMMIT;
