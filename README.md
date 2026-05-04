# CEP API

> API REST em **Java 21 + Spring Boot** para consulta de CEP com registro de logs em PostgreSQL.

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Como Executar](#como-executar)
- [Endpoints](#endpoints)
- [CEPs Disponíveis no Mock](#ceps-disponíveis-no-mock)
- [Schema do Banco](#schema-do-banco)

---

##   Sobre o Projeto

A aplicação é uma API REST que:

Deverá prover a capacidade de realizar as operações de busca de
cep em uma api externa (de preferência para fazer a api mocada com Wiremock,
Mockoon ou similar);

Os logs das consultas precisam ser gravados em base de dados, com o horário da
   consulta e os dados que retornaram da api

1. Recebe uma requisição de consulta de CEP
2. Busca os dados em uma **API externa mockada com WireMock**
3. **Grava um log** da consulta no banco de dados (com horário, dados retornados e status)
4. Permite **consultar o histórico** de logs com filtros por CEP, status e período

---

##   Arquitetura

```
┌─────────────────────────────────────────────────────────────┐
│                    Entrada                                  │
│   controller/CepController                                  │
│   controller/LogHistoricoController                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                     Domínio                                 │
│                                                             │
│   domain/port/                                              │
│     CepGateway        (port de saída — buscar CEP)          │
│     CepLogPort        (port de saída — salvar log)          │
│     LogHistoricoPort  (port de saída — consultar histórico) │
│                                                             │
│   domain/service/                                           │
│     CepService          (caso de uso: consultar CEP)        │
│     LogHistoricoService (caso de uso: histórico)            │
│                                                             │
│   domain/model/                                             │
│     CepData, CepLog, ConsultaStatus                         │
└──────────┬──────────────────────────┬───────────────────────┘
           │                          │ 
┌──────────▼────────────┐  ┌──────────▼────────────────────────┐
│   infra/http/         │  │   infra/persistence/              │
│   WireMockCepGateway  │  │   CepLogPersistenceAdapter        │
│                       │  │   CepLogEntity (@Entity)          │
│     WireMock :8089    │  │   LogHistoricoPersistenceAdapter  │
└───────────────────────┘  │                                   │
                           │     PostgreSQL :5432              │
                           └───────────────────────────────────┘
```

---


##   Tecnologias

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.3 | Framework web e DI |
| Spring Data JPA | 3.3 | Persistência |
| PostgreSQL | 16 | Banco de dados |
| Flyway | 10 | Migrações de schema |
| WireMock | 3.6 | Mock da API de CEP |
| Docker Compose | — | Orquestração local |
| Lombok | — | Redução de boilerplate |

---

##  Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Git instalado

### 1. Clonar o repositório

```
git clone https://github.com/Joabss/cep-api.git
```

### 2. Subir o ambiente completo

```
docker compose up --build
```

- **PostgreSQL** na porta `5432`
- **WireMock** na porta `8089` (mock da API de CEP)
- **cep-api** na porta `8080`

### 3. Verificar que está rodando

```
curl http://localhost:8080/api/v1/health
# Retorna: OK
```

### Comandos úteis

```
# Rodar em background
docker compose up --build -d

# Ver logs da aplicação em tempo real
docker compose logs -f cep-api

# Parar tudo
docker compose down

# Parar e apagar dados do banco
docker compose down -v
```

---

##   Endpoints

### Consultar CEP

```
GET /api/v1/cep/{cep}
```

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `cep` | path | ✅ | CEP com ou sem máscara (`01310100` ou `01310-100`) |

**Exemplo:**
```bash
curl http://localhost:8080/api/v1/cep/01310100
```

**Resposta 200:**
```json
{
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "complemento": "de 1 a 610 - lado par",
  "bairro": "Bela Vista",
  "localidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308",
  "ddd": "11"
}
```

**Resposta 404:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "CEP não encontrado: 99999999",
  "timestamp": "2024-06-15T14:30:00"
}
```

---


### Health Check

```
GET /api/v1/health
```

```bash
curl http://localhost:8080/api/v1/health
# Retorna: OK
```
---

### Consultar Histórico de Logs

```
GET /api/v1/logs
```


| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `cep` | query | Filtra por CEP exato |
| `status` | query | `SUCCESS`, `NOT_FOUND` ou `ERROR` |
| `de` | query | Data inicial no formato `YYYY-MM-DD` |
| `ate` | query | Data final no formato `YYYY-MM-DD` |

**Exemplos:**
```
# Todos os logs
curl http://localhost:8080/api/v1/logs

# Por CEP
curl "http://localhost:8080/api/v1/logs?cep=01310100"

# Por status
curl "http://localhost:8080/api/v1/logs?status=NOT_FOUND"

# Por período
curl "http://localhost:8080/api/v1/logs?de=2024-06-01&ate=2024-06-30"

# Combinando filtros
curl "http://localhost:8080/api/v1/logs?cep=01310100&status=SUCCESS&de=2024-01-01&ate=2024-12-31"
```

**Resposta 200:**
```json
[
  {
    "id": 3,
    "cep": "01310100",
    "status": "SUCCESS",
    "consultedAt": "2024-06-15T14:32:01"
  },
  {
    "id": 2,
    "cep": "99999999",
    "status": "NOT_FOUND",
    "consultedAt": "2024-06-15T14:30:45"
  }
]
```
---

##   CEPs Disponíveis no Mock

| CEP | Endereço | Cidade | UF |
|-----|----------|--------|----|
| `01310100` | Av. Paulista | São Paulo | SP |
| `20040020` | Av. Rio Branco | Rio de Janeiro | RJ |
| `30130110` | Av. Afonso Pena | Belo Horizonte | MG |
| `99999999` | — | Retorna não encontrado | — |

Para adicionar novos CEPs, edite `wiremock/mappings/cep-mappings.json` e reinicie o container do WireMock.

---

##   Schema do Banco

```sql
CREATE TABLE cep_log (
    id           BIGSERIAL    PRIMARY KEY,
    cep          VARCHAR(9)   NOT NULL,
    logradouro   VARCHAR(255),
    complemento  VARCHAR(255),
    bairro       VARCHAR(255),
    localidade   VARCHAR(255),
    uf           VARCHAR(2),
    ibge         VARCHAR(20),
    status       VARCHAR(20)  NOT NULL,   -- SUCCESS | NOT_FOUND | ERROR
    error_msg    VARCHAR(500),
    consulted_at TIMESTAMP    NOT NULL   -- horário exato da consulta
);
```
