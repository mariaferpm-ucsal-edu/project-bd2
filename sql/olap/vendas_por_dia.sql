SELECT
    t.ano,
    t.mes,
    t.dia,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_tempo AS t
    ON f.sk_tempo = t.sk_tempo
GROUP BY
    t.ano,
    t.mes,
    t.dia
ORDER BY
    t.ano,
    t.mes,
    t.dia;