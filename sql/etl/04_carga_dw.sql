
-- ============================================================
-- ETL - REDE DE FARMACIAS
-- 04 - CARGA DOS DATA WAREHOUSES
-- ============================================================

BEGIN;

-- ============================================================
-- PARTE 1 - DIMENSAO TEMPO
-- Chave substituta no formato AAAAMMDD
-- ============================================================

INSERT INTO dw.dim_tempo (
    sk_tempo, data, dia, mes, nome_mes,
    trimestre, semestre, ano, dia_semana
)
SELECT DISTINCT
    TO_CHAR(data_venda, 'YYYYMMDD')::INTEGER,
    data_venda,
    EXTRACT(DAY FROM data_venda)::INTEGER,
    EXTRACT(MONTH FROM data_venda)::INTEGER,
    CASE EXTRACT(MONTH FROM data_venda)::INTEGER
        WHEN 1 THEN 'Janeiro'
        WHEN 2 THEN 'Fevereiro'
        WHEN 3 THEN 'Marco'
        WHEN 4 THEN 'Abril'
        WHEN 5 THEN 'Maio'
        WHEN 6 THEN 'Junho'
        WHEN 7 THEN 'Julho'
        WHEN 8 THEN 'Agosto'
        WHEN 9 THEN 'Setembro'
        WHEN 10 THEN 'Outubro'
        WHEN 11 THEN 'Novembro'
        WHEN 12 THEN 'Dezembro'
    END,
    EXTRACT(QUARTER FROM data_venda)::INTEGER,
    CASE
        WHEN EXTRACT(MONTH FROM data_venda) <= 6 THEN 1
        ELSE 2
    END,
    EXTRACT(YEAR FROM data_venda)::INTEGER,
    CASE EXTRACT(ISODOW FROM data_venda)::INTEGER
        WHEN 1 THEN 'Segunda'
        WHEN 2 THEN 'Terca'
        WHEN 3 THEN 'Quarta'
        WHEN 4 THEN 'Quinta'
        WHEN 5 THEN 'Sexta'
        WHEN 6 THEN 'Sabado'
        WHEN 7 THEN 'Domingo'
    END
FROM staging.stg_fato_venda
ON CONFLICT (data) DO NOTHING;

-- ============================================================
-- PARTE 2 - CLIENTES NO MODELO ESTRELA
-- SCD TIPO 2
-- ============================================================

-- Encerra a versao atual quando os dados mudarem
UPDATE dw.dim_cliente d
SET
    fim_vigencia = CURRENT_TIMESTAMP,
    atual = FALSE
FROM staging.stg_cliente s
WHERE d.id_origem = s.id_origem
  AND d.atual = TRUE
  AND (
      d.nome IS DISTINCT FROM s.nome
      OR d.cidade IS DISTINCT FROM s.cidade
      OR d.uf IS DISTINCT FROM s.uf
  );

-- Insere clientes novos ou novas versoes
INSERT INTO dw.dim_cliente (
    id_origem, nome, cidade, uf,
    inicio_vigencia, fim_vigencia, atual
)
SELECT
    s.id_origem,
    s.nome,
    s.cidade,
    s.uf,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE
FROM staging.stg_cliente s
WHERE NOT EXISTS (
    SELECT 1
    FROM dw.dim_cliente d
    WHERE d.id_origem = s.id_origem
      AND d.atual = TRUE
);

-- ============================================================
-- PARTE 3 - PRODUTOS NO MODELO ESTRELA
-- ============================================================

INSERT INTO dw.dim_produto (
    id_origem, nome, apresentacao, categoria, laboratorio
)
SELECT
    id_origem,
    nome,
    apresentacao,
    COALESCE(categoria_nome, 'Nao informado'),
    COALESCE(laboratorio_nome, 'Nao informado')
FROM staging.stg_produto
ON CONFLICT (id_origem) DO UPDATE SET
    nome = EXCLUDED.nome,
    apresentacao = EXCLUDED.apresentacao,
    categoria = EXCLUDED.categoria,
    laboratorio = EXCLUDED.laboratorio;

-- ============================================================
-- PARTE 4 - LOJAS NO MODELO ESTRELA
-- ============================================================

INSERT INTO dw.dim_loja (
    id_origem, nome, cidade, uf
)
SELECT
    id_origem, nome, cidade, uf
FROM staging.stg_loja
ON CONFLICT (id_origem) DO UPDATE SET
    nome = EXCLUDED.nome,
    cidade = EXCLUDED.cidade,
    uf = EXCLUDED.uf;

-- ============================================================
-- PARTE 5 - CLIENTES NO MODELO FLOCO
-- SCD TIPO 2
-- ============================================================

UPDATE dw_floco.dim_cliente d
SET
    fim_vigencia = CURRENT_TIMESTAMP,
    atual = FALSE
FROM staging.stg_cliente s
WHERE d.id_origem = s.id_origem
  AND d.atual = TRUE
  AND (
      d.nome IS DISTINCT FROM s.nome
      OR d.cidade IS DISTINCT FROM s.cidade
      OR d.uf IS DISTINCT FROM s.uf
  );

INSERT INTO dw_floco.dim_cliente (
    id_origem, nome, cidade, uf,
    inicio_vigencia, fim_vigencia, atual
)
SELECT
    s.id_origem,
    s.nome,
    s.cidade,
    s.uf,
    CURRENT_TIMESTAMP,
    NULL,
    TRUE
FROM staging.stg_cliente s
WHERE NOT EXISTS (
    SELECT 1
    FROM dw_floco.dim_cliente d
    WHERE d.id_origem = s.id_origem
      AND d.atual = TRUE
);

-- ============================================================
-- PARTE 6 - CATEGORIAS E LABORATORIOS NO FLOCO
-- ============================================================

INSERT INTO dw_floco.dim_categoria (id_origem, nome)
SELECT id_origem, nome
FROM staging.stg_categoria
ON CONFLICT (id_origem) DO UPDATE SET
    nome = EXCLUDED.nome;

INSERT INTO dw_floco.dim_laboratorio (id_origem, nome)
SELECT id_origem, nome
FROM staging.stg_laboratorio
ON CONFLICT (id_origem) DO UPDATE SET
    nome = EXCLUDED.nome;

-- ============================================================
-- PARTE 7 - PRODUTOS NO MODELO FLOCO
-- ============================================================

INSERT INTO dw_floco.dim_produto (
    id_origem, nome, apresentacao,
    sk_categoria, sk_laboratorio
)
SELECT
    p.id_origem,
    p.nome,
    p.apresentacao,
    c.sk_categoria,
    l.sk_laboratorio
FROM staging.stg_produto p
JOIN dw_floco.dim_categoria c
    ON c.id_origem = p.categoria_id_origem
JOIN dw_floco.dim_laboratorio l
    ON l.id_origem = p.laboratorio_id_origem
ON CONFLICT (id_origem) DO UPDATE SET
    nome = EXCLUDED.nome,
    apresentacao = EXCLUDED.apresentacao,
    sk_categoria = EXCLUDED.sk_categoria,
    sk_laboratorio = EXCLUDED.sk_laboratorio;

-- ============================================================
-- PARTE 8 - ESTADO, CIDADE E LOJA NO MODELO FLOCO
-- ============================================================

INSERT INTO dw_floco.dim_estado (uf)
SELECT DISTINCT COALESCE(NULLIF(UPPER(BTRIM(uf)), ''), 'NI')
FROM staging.stg_loja
ON CONFLICT (uf) DO NOTHING;

INSERT INTO dw_floco.dim_cidade (sk_estado, nome)
SELECT DISTINCT
    e.sk_estado,
    COALESCE(NULLIF(BTRIM(l.cidade), ''), 'Nao informado')
FROM staging.stg_loja l
JOIN dw_floco.dim_estado e
    ON e.uf = COALESCE(NULLIF(UPPER(BTRIM(l.uf)), ''), 'NI')
ON CONFLICT (sk_estado, nome) DO NOTHING;

INSERT INTO dw_floco.dim_loja (
    id_origem, nome, sk_cidade
)
SELECT
    l.id_origem,
    l.nome,
    c.sk_cidade
FROM staging.stg_loja l
JOIN dw_floco.dim_estado e
    ON e.uf = COALESCE(NULLIF(UPPER(BTRIM(l.uf)), ''), 'NI')
JOIN dw_floco.dim_cidade c
    ON c.sk_estado = e.sk_estado
   AND c.nome = COALESCE(NULLIF(BTRIM(l.cidade), ''), 'Nao informado')
ON CONFLICT (id_origem) DO UPDATE SET
    nome = EXCLUDED.nome,
    sk_cidade = EXCLUDED.sk_cidade;

-- ============================================================
-- PARTE 9 - FATO DE VENDAS NO MODELO ESTRELA
-- ============================================================

INSERT INTO dw.fato_venda (
    item_venda_id_origem,
    venda_id_origem,
    sk_tempo,
    sk_produto,
    sk_cliente,
    sk_loja,
    quantidade,
    valor_bruto,
    desconto_valor,
    valor_liquido
)
SELECT
    f.item_venda_id_origem,
    f.venda_id_origem,
    t.sk_tempo,
    p.sk_produto,
    c.sk_cliente,
    l.sk_loja,
    f.quantidade,
    f.valor_bruto,
    f.desconto_valor,
    f.valor_liquido
FROM staging.stg_fato_venda f
JOIN dw.dim_tempo t
    ON t.data = f.data_venda
JOIN dw.dim_produto p
    ON p.id_origem = f.produto_id_origem
JOIN dw.dim_cliente c
    ON c.id_origem = f.cliente_id_origem
   AND c.atual = TRUE
JOIN dw.dim_loja l
    ON l.id_origem = f.loja_id_origem
ON CONFLICT (item_venda_id_origem) DO NOTHING;

-- ============================================================
-- PARTE 10 - FATO DE VENDAS NO MODELO FLOCO
-- ============================================================

INSERT INTO dw_floco.fato_venda (
    item_venda_id_origem,
    venda_id_origem,
    sk_tempo,
    sk_produto,
    sk_cliente,
    sk_loja,
    quantidade,
    valor_bruto,
    desconto_valor,
    valor_liquido
)
SELECT
    f.item_venda_id_origem,
    f.venda_id_origem,
    t.sk_tempo,
    p.sk_produto,
    c.sk_cliente,
    l.sk_loja,
    f.quantidade,
    f.valor_bruto,
    f.desconto_valor,
    f.valor_liquido
FROM staging.stg_fato_venda f
JOIN dw_floco.dim_tempo t
    ON t.data = f.data_venda
JOIN dw_floco.dim_produto p
    ON p.id_origem = f.produto_id_origem
JOIN dw_floco.dim_cliente c
    ON c.id_origem = f.cliente_id_origem
   AND c.atual = TRUE
JOIN dw_floco.dim_loja l
    ON l.id_origem = f.loja_id_origem
ON CONFLICT (item_venda_id_origem) DO NOTHING;

COMMIT;
