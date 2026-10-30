# Como contribuir

O historico de commits e a evidencia da participacao individual: **todos os 5 integrantes devem commitar**.

## Fluxo
1. `git checkout -b feature/<papel>-<assunto>` (ex.: `feature/orm-entidades-venda`)
2. Commits pequenos e frequentes, **com o seu proprio usuario Git**
   (`git config user.name "Seu Nome"` e `git config user.email "voce@email.com"`).
3. Abra um Pull Request para `main`; o Lider revisa e faz o merge.

## Mensagens de commit
`tipo(escopo): descricao curta`
Tipos: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `sql`, `etl`.
Exemplos: `feat(model): adiciona entidade Venda`, `etl(dim): gera dim_tempo`.

## Donos por pasta
| Papel | Pastas |
|---|---|
| Arquiteto de dados | `diagramas`, `sql/oltp/`, `sql/dw/` |
| Desenvolvedor ORM | `src/main/java/.../model`, `dao`, `sql/oltp` |
| Engenheiro de ETL | `sql/etl` (e classes ETL em Java, se houver) |
| Analista de BI | `sql/olap`, `mineracao/` |
| Lider | `relatorio`, `README.md`, revisao de PRs |
