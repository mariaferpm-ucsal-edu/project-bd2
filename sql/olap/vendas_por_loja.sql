SELECT
    l.uf AS estado,
    l.cidade,
    l.id_origem AS codigo_loja,
    l.nome AS loja,
    SUM(f.valor_liquido) AS faturamento_liquido
FROM dw.fato_venda AS f
JOIN dw.dim_loja AS l
    ON f.sk_loja = l.sk_loja
GROUP BY
    l.uf,
    l.cidade,
    l.id_origem,
    l.nome
ORDER BY
    l.uf,
    l.cidade,
    l.nome,
    l.id_origem;