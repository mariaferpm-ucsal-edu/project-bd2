SELECT
    t.ano,
    l.uf AS estado,
    SUM(f.quantidade) AS quantidade_vendida,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_tempo AS t
    ON f.sk_tempo = t.sk_tempo
JOIN dw.dim_loja AS l
    ON f.sk_loja = l.sk_loja
WHERE t.ano IN (2024, 2025)
  AND l.uf IN ('BA', 'SE')
GROUP BY
    t.ano,
    l.uf
ORDER BY
    t.ano,
    l.uf;