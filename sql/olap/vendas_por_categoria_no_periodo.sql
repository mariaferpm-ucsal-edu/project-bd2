SELECT
    t.ano,
    t.mes,
    p.categoria,
    SUM(f.quantidade) AS quantidade_vendida,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_tempo AS t
    ON f.sk_tempo = t.sk_tempo
JOIN dw.dim_produto AS p
    ON f.sk_produto = p.sk_produto
WHERE t.ano = 2025
  AND t.mes BETWEEN 1 AND 6
  AND p.categoria IN ('Medicamentos', 'Higiene')
GROUP BY
    t.ano,
    t.mes,
    p.categoria
ORDER BY
    t.ano,
    t.mes,
    p.categoria;