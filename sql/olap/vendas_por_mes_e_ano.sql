SELECT
    t.ano,
    t.mes,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_tempo AS t
    ON f.sk_tempo = t.sk_tempo
GROUP BY ROLLUP(t.ano, t.mes)
ORDER BY
    t.ano NULLS LAST,
    t.mes NULLS LAST;