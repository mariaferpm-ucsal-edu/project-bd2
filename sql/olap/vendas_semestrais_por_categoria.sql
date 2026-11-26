SELECT
    t.ano,
    p.categoria,
    SUM(CASE WHEN t.semestre = 1
        THEN f.valor_liquido ELSE 0 END) AS primeiro_semestre,
    SUM(CASE WHEN t.semestre = 2
        THEN f.valor_liquido ELSE 0 END) AS segundo_semestre,
    SUM(f.valor_liquido) AS total_anual
FROM dw.fato_venda AS f
JOIN dw.dim_tempo AS t
    ON f.sk_tempo = t.sk_tempo
JOIN dw.dim_produto AS p
    ON f.sk_produto = p.sk_produto
GROUP BY
    t.ano,
    p.categoria
ORDER BY
    t.ano,
    p.categoria;