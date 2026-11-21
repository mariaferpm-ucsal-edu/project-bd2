SELECT
    l.uf AS estado,
    l.cidade,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_loja AS l
    ON f.sk_loja = l.sk_loja
GROUP BY ROLLUP(l.uf, l.cidade)
ORDER BY
    l.uf NULLS LAST,
    l.cidade NULLS LAST;