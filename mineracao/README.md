# Mineração das compras da rede de farmácias

Esta etapa utiliza Apriori para encontrar pares de produtos frequentes e gerar regras de associação. As conclusões de negócio devem ser escritas a partir dos resultados obtidos após a carga do DW.

## Situação da entrega

- Conexão local com PostgreSQL verificada.
- Preparação e algoritmo testados com dados fictícios.
- Execução no banco do projeto: DW sem itens de venda no momento da preparação.
- Análise com dados do projeto, justificativa final dos parâmetros e três conclusões de negócio: pendentes da carga pelo ETL.

## Ambiente e instalação

Execute os comandos na raiz do repositório, no PowerShell. O ambiente fica fora da pasta versionada.

```powershell
py -3.13 -m venv "$env:USERPROFILE\venvs\project-bd2"
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" -m pip install -r .\mineracao\requirements.txt
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" -m pip check
```

O requisito de SQLAlchemy foi atualizado para `SQLAlchemy>=2.0.42,<3.0`, para permitir o uso com o pandas atual.

## Fluxo de execução

1. Carregue o OLTP e execute o ETL da equipe para preencher o modelo estrela.
2. Confira a conexão e as contagens:

```powershell
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" .\mineracao\testar_conexao.py
```

3. Extraia e prepare as compras:

```powershell
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" .\mineracao\preparar_dados.py
```

4. Execute o Apriori:

```powershell
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" .\mineracao\executar_apriori.py --suporte 0.02 --confianca 0.60 --lift 1
```

O preparo solicita a senha no terminal. A senha não é gravada nos scripts. Os padrões locais são usuário `postgres`, host `localhost`, porta `5432` e banco `vendas`. O preparo também permite configurar `DB_USER`, `DB_HOST`, `DB_PORT` e `DB_NAME` no ambiente do processo; a senha continua sendo solicitada.

Quando o DW está vazio, o preparo informa a pendência e não atualiza os arquivos de dados. Quando a base preparada ainda não existe, o algoritmo informa essa condição e não gera resultados. Confira as mensagens de cada execução antes de interpretar arquivos existentes.

## Preparação e unidade de análise

A consulta `compras_para_mineracao.sql` relaciona `dw.fato_venda` a `dw.dim_produto`.

- Cada `codigo_venda` corresponde a uma cesta de compra.
- O produto é identificado por `codigo_produto`; produtos com o mesmo nome permanecem separados.
- Repetições do mesmo produto na mesma compra são removidas.
- A matriz registra presença ou ausência, sem ponderar pela quantidade de unidades.
- Compras com apenas um produto são mantidas no denominador do suporte.
- Identificadores ou nomes ausentes e códigos com nomes conflitantes geram uma mensagem de erro para revisão da extração.

Confirme com a equipe de ETL quais vendas foram incluídas no DW, especialmente o tratamento de cancelamentos. As conclusões devem informar o período, o universo de compras e as limitações da base.

## Parâmetros iniciais

| Parâmetro | Valor | Interpretação |
|---|---:|---|
| Suporte mínimo | 0,02 | A combinação precisa ocorrer em pelo menos 2% das compras. |
| Confiança mínima | 0,60 | Entre as compras com A, pelo menos 60% também contêm B. |
| Limite de lift | 1 | Somente regras com lift estritamente maior que 1 são selecionadas. |
| Tamanho máximo do conjunto | 2 | A análise inicial utiliza pares de produtos. |

Esses valores são exploratórios. Sua justificativa final depende da quantidade de compras, das frequências dos produtos e da comparação entre execuções. Cada execução registra os parâmetros utilizados.

O suporte de A → B é a proporção de compras contendo A e B. A confiança é suporte(A e B) dividido por suporte(A). O lift é confiança(A → B) dividida por suporte(B). Uma associação positiva observada não demonstra causalidade.

## Arquivos gerados

| Pasta | Conteúdo |
|---|---|
| `dados_preparados/` | Compras, catálogo, matriz de cestas e contagens do preparo. |
| `resultados_apriori/` | Base utilizada, catálogo, matriz de cestas, combinações frequentes, regras e resumo da execução. |

O arquivo `parametros_e_resumo.json` registra os parâmetros, as quantidades de compras e produtos, as regras selecionadas, versões das bibliotecas e o SHA-256 do arquivo de entrada. As proporções nos CSVs usam escala de 0 a 1.

Se nenhuma regra atender aos critérios, esse resultado é registrado. Ele deve ser discutido considerando as características da base e os parâmetros escolhidos.

## Notebook

O notebook `analise_compras.ipynb` documenta o método e executa o mesmo script de mineração. Primeiro, gere a base com `preparar_dados.py`.

Registre o ambiente e abra o notebook:

```powershell
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" -m ipykernel install --user --name project-bd2 --display-name "Python (BD2)"
& "$env:USERPROFILE\venvs\project-bd2\Scripts\python.exe" -m notebook .\mineracao\analise_compras.ipynb
```

Escolha o kernel **Python (BD2)** e execute as células na ordem. Sem base preparada, o notebook registra a pendência. Depois do ETL, execute novamente e salve o notebook com as saídas.

## Três conclusões de negócio

Preencha a interpretação no notebook após a execução com dados do projeto. Para cada conclusão, registre:

1. O achado observado, com produtos e período.
2. A evidência: suporte, confiança, lift e quantidade de compras, quando houver regra.
3. A decisão de negócio sugerida e as limitações da interpretação.

As três conclusões não precisam corresponder a três regras diferentes. Resultados sem associações consistentes também devem ser discutidos com evidências.

## Verificações de desenvolvimento

Foram utilizados dados fictícios para verificar os cálculos: oito compras, com A e B presentes juntos em três delas e cada produto presente em quatro. As duas regras A → B e B → A apresentaram suporte 37,5%, confiança 75% e lift 1,5.

Também foram verificados: manutenção de compras unitárias, remoção de repetições, distinção de produtos com nomes iguais, limites dos parâmetros, ausência de combinações frequentes, ausência de regras, exportação e execução sem base preparada.

## Referências técnicas

- [Apriori — documentação do mlxtend](https://rasbt.github.io/mlxtend/user_guide/frequent_patterns/apriori/)
- [Regras de associação — documentação do mlxtend](https://rasbt.github.io/mlxtend/user_guide/frequent_patterns/association_rules/)
- [Dependências do pandas](https://pandas.pydata.org/docs/getting_started/install.html)

