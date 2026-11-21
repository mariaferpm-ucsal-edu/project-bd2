SELECT
    p.categoria,
    SUM(f.quantidade) AS quantidade_vendida,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_loja AS l
    ON f.sk_loja = l.sk_loja
JOIN dw.dim_produto AS p
    ON f.sk_produto = p.sk_produto
WHERE l.uf = 'BA'
GROUP BY
    p.categoria
ORDER BY
    faturamento_liquido DESC,
    p.categoria;