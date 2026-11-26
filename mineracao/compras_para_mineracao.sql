SELECT DISTINCT
    f.venda_id_origem AS codigo_venda,
    p.id_origem AS codigo_produto,
    p.nome AS produto
FROM dw.fato_venda AS f
JOIN dw.dim_produto AS p
    ON f.sk_produto = p.sk_produto
ORDER BY
    codigo_venda,
    codigo_produto;