# Checkpoint 03 - Arquitetura SOA e Web Services - 3ESPV
API REST para cadastro e gestao de instrutores e alunos.

## Dados
Felipe Soares Xavier - RM 556931
Leonardo Corrêa de Mello - RM 555573
Pedro Visconti Guidotte - RM 556630

## Requisitos

- Java 25 (conforme `pom.xml`)
- Maven Wrapper (`mvnw`)
- H2 em memoria (configurado em `application.properties`)

## Como executar

```powershell
Push-Location "C:\Users\...\AutoEscolaCP03\Checkpoint03"
.\\mvnw spring-boot:run
Pop-Location
```

## Endpoints

- POST `/instrutores`
- GET `/instrutores`
- PUT `/instrutores/{id}`
- DELETE `/instrutores/{id}`
- POST `/alunos`
- GET `/alunos`
- PUT `/alunos/{id}`
- DELETE `/alunos/{id}`
