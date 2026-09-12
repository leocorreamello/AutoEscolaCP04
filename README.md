# Checkpoint 04 - Arquitetura SOA e Web Services - 3ESPV
API REST da auto escola: cadastro de instrutores e alunos, agendamento e cancelamento de instrucoes,
gestao de usuarios e autenticacao via token JWT.

## Dados
Felipe Soares Xavier - RM 556931 </br>
Leonardo Corrêa de Mello - RM 555573 </br>
Pedro Visconti Guidotte - RM 556630 </br>

## Requisitos

- Java 25 (conforme `pom.xml`)
- Maven Wrapper (`mvnw`)
- H2 em memoria (configurado em `application.properties`)

## Como executar

```powershell
Push-Location "C:\Users\...\AutoEscolaCP04\Checkpoint03"
.\\mvnw spring-boot:run
Pop-Location
```

Para rodar os testes: `.\mvnw test`

## Autenticacao

Todas as rotas, exceto `POST /login`, exigem o header `Authorization: Bearer <token>`.

Usuario administrador criado pela migracao `V4`:

| Login   | Senha      | Perfil |
|---------|------------|--------|
| `admin` | `admin123` | ADMIN  |

As senhas sao armazenadas criptografadas com BCrypt. O segredo do JWT pode ser definido pela
variavel de ambiente `JWT_SECRET` (ha um valor padrao apenas para desenvolvimento).

```http
POST /login
{"login": "admin", "senha": "admin123"}
```

## Endpoints

### Usuarios
| Metodo | Rota                  | Acesso        | Descricao                              |
|--------|-----------------------|---------------|----------------------------------------|
| POST   | `/usuarios`           | ADMIN         | Cadastra usuario (`login`, `senha`, `perfil`) |
| GET    | `/usuarios`           | ADMIN         | Lista usuarios (paginado)              |
| PUT    | `/usuarios/{id}`      | ADMIN         | Atualiza o perfil (`ADMIN` ou `USER`)  |
| DELETE | `/usuarios/{id}`      | ADMIN         | Exclui usuario                         |
| PATCH  | `/usuarios/me/senha`  | Autenticado   | Altera a propria senha (`senhaAtual`, `novaSenha`) |

O administrador nao pode excluir o proprio usuario nem remover o proprio perfil de ADMIN.

### Instrutores
- POST `/instrutores`
- GET `/instrutores` (ordenado por nome, 10 por pagina, apenas ativos)
- PUT `/instrutores/{id}` (apenas nome, telefone e endereco; enviar `email`, `cnh` ou `especialidade` retorna 400)
- DELETE `/instrutores/{id}` (exclusao logica: marca como inativo)

### Alunos
- POST `/alunos`
- GET `/alunos` (ordenado por nome, 10 por pagina, apenas ativos)
- PUT `/alunos/{id}` (apenas nome, telefone e endereco; enviar `email` ou `cpf` retorna 400)
- DELETE `/alunos/{id}` (exclusao logica: marca como inativo)

### Instrucoes
| Metodo | Rota                            | Descricao |
|--------|---------------------------------|-----------|
| POST   | `/instrucoes`                   | Agenda instrucao (`idAluno`, `idInstrutor` opcional, `dataHora`) |
| GET    | `/instrucoes`                   | Lista instrucoes (ordenado por data/hora, 10 por pagina) |
| PATCH  | `/instrucoes/{id}/cancelamento` | Cancela instrucao (`motivo`) |

```http
POST /instrucoes
{"idAluno": 1, "idInstrutor": 1, "dataHora": "2030-01-08T10:00:00"}

PATCH /instrucoes/1/cancelamento
{"motivo": "ALUNO_DESISTIU"}
```

## Regras de negocio

**Agendamento**
- Funcionamento de segunda a sabado, das 06:00 as 21:00. Como a instrucao dura 1 hora, o ultimo inicio possivel e 20:00.
- Antecedencia minima de 30 minutos.
- Aluno e instrutor precisam estar ativos.
- No maximo 2 instrucoes por dia para o mesmo aluno.
- O instrutor nao pode ter outra instrucao que se sobreponha a janela de 1 hora (ex.: 10:00 conflita com 10:30, mas nao com 11:00). A mesma regra vale para o aluno.
- Se o instrutor nao for informado, o sistema sorteia um instrutor ativo disponivel na data/hora.
- Instrucoes canceladas nao contam para os limites acima.

**Cancelamento**
- Motivo obrigatorio: `ALUNO_DESISTIU`, `INSTRUTOR_CANCELOU` ou `OUTROS`.
- Antecedencia minima de 24 horas.
- A instrucao nao e apagada: fica registrada como cancelada, com o motivo.
