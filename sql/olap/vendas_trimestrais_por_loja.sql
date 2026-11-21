SELECT
    t.ano,
    l.id_origem AS codigo_loja,
    l.nome AS loja,
    SUM(CASE WHEN t.trimestre = 1
        THEN f.valor_liquido ELSE 0 END) AS trimestre_1,
    SUM(CASE WHEN t.trimestre = 2
        THEN f.valor_liquido ELSE 0 END) AS trimestre_2,
    SUM(CASE WHEN t.trimestre = 3
        THEN f.valor_liquido ELSE 0 END) AS trimestre_3,
    SUM(CASE WHEN t.trimestre = 4
        THEN f.valor_liquido ELSE 0 END) AS trimestre_4,
    SUM(f.valor_liquido) AS total_anual
FROM dw.fato_venda AS f
JOIN dw.dim_tempo AS t
    ON f.sk_tempo = t.sk_tempo
JOIN dw.dim_loja AS l
    ON f.sk_loja = l.sk_loja
GROUP BY
    t.ano,
    l.id_origem,
    l.nome
ORDER BY
    t.ano,
    l.nome,
    l.id_origem;