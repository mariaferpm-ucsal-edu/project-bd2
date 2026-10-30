# BD II – Sistema de Vendas (OLTP) + Data Warehouse

Projeto da 2ª Unidade: sistema transacional com **JPA 2.1 / Hibernate 5.2.10.Final** sobre **PostgreSQL**,
Data Warehouse (estrela e floco de neve), ETL, OLAP e mineração com Python.

> Domínio da equipe: _a definir_ · Integrantes: _Maria Fernanda Maia, Victor Farias Novais, Rafael Rivas Garcia, Bernardo Barbosa Garcia, EMilly Maciel Araujo_

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
mvn clean compile             # baixa as dependências
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


