# BD II – Sistema de Vendas Farmacêutico (OLTP) + Data Warehouse

> Domínio da equipe: _Rede de farmácias_ · Integrantes: _Maria Fernanda Maia, Victor Farias Novais, Rafael Rivas Garcia, Bernardo Barbosa Garcia, EMilly Maciel Araujo_

## Stack
| Componente | Versão |
|---|---|
| JPA | 2.1 (`javax.persistence`) |
| ORM | Hibernate ORM 5.2.10.Final |
| Java | JDK 8 |
| IDE | Eclipse Neon (Java EE Developers) |
| Build | Maven |
| SGBD | PostgreSQL (OLTP e DW, esquemas separados) |
| Mineração | Python (scikit-learn, mlxtend) |

## Início rápido
```bash
git clone 
cp .env.example .env
docker compose up -d          # PostgreSQL com os esquemas oltp, 
mvn clean install    # dependendo da complexidade do projeto pode precisar de -DskipTests         
mvn exec:java              
mvn test                      # precisa do banco ok
```
**Rodando apenas com Postgres local (Sem docker)**

Instale o PostgreSQL, crie o banco `vendas` e o usuário `bd2`/`bd2`, e rode
`docker/init/01_schemas.sql`. Para outras credenciais use as variáveis `DB_URL`, `DB_USER`, `DB_PASSWORD`.

## Eclipse Neon
1. `File > Import > Maven > Existing Maven Projects` e selecione a pasta.
2. `Project > Properties > Java Compiler`: nível 1.8; JRE: JDK 8 (`Window > Preferences > Java > Installed JREs`).
3. `Properties > Java Compiler > Annotation Processing`: habilitar (gera o metamodelo `Entidade_` para a Criteria API).
4. Se o Hibernate reclamar de versão, confira `mvn -v` (deve mostrar Java 1.8).

## Esquemas do banco
| Esquema | Uso |
|---|---|
| `oltp` | tabelas das entidades JPA (`hbm2ddl.auto=update`) |
| `staging` | área de staging do ETL |
| `dw` | modelo estrela |
| `dw_floco` | modelo floco de neve |

## Fluxo de contribuições (integrantes)
1. `git checkout -b feature/<papel>-<assunto>` (ex.: `feature/orm-entidades-venda`)
2. Commits pequenos e frequentes, **com o seu proprio usuario Git**
   (`git config user.name "Seu Nome"` e `git config user.email "voce@email.com"`).
3. Abra um Pull Request para `main`; o Lider revisa e faz o merge.
