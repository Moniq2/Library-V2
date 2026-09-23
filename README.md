# Library V2

Sistema de gerenciamento de biblioteca, composto por uma API REST e um cliente desktop que consome essa API.

## Sobre o projeto

O sistema permite o controle de usuários (professores e alunos), livros, exemplares e empréstimos, com regras de negócio aplicadas conforme o tipo de usuário e a disponibilidade de exemplares. A API concentra toda a lógica de negócio e persistência; o cliente desktop é responsável apenas pela interface e consome a API via requisições HTTP.

## Estrutura do repositório

O projeto é organizado como monorepo, com dois módulos independentes, cada um com seu próprio `pom.xml`:

```
Library-V2/
├── Biblioteca-api/        # API REST (Spring Boot)
└── Biblioteca-desktop/    # Cliente desktop (JavaFX)
```

### Biblioteca-api

API responsável pelas regras de negócio e persistência de dados.

- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security com autenticação via JWT
- Documentação da API via Swagger / OpenAPI (springdoc)
- ModelMapper para conversão entre entidades e DTOs
- Lombok
- Banco de dados MySQL
- Java 17

Principais pacotes: `controller`, `dto`, `entity`, `exception`, `repository`, `security`, `service`.

Entidades principais: `Usuario` (com subtipos `Professor` e `Aluno`), `Livro`, `Exemplar` e `Emprestimo`. Exceções de negócio dedicadas para cada regra (limite de empréstimos, renovações, exemplares indisponíveis, credenciais inválidas, entre outras), tratadas por um `GlobalExceptionHandler`.

### Biblioteca-desktop

Cliente desktop responsável pela interface com o usuário, consumindo a API por meio de requisições HTTP (Jackson para serialização JSON).

- JavaFX
- FXML para definição de telas
- Controllers dedicados por tela
- Clients HTTP para comunicação com a API (`UsuarioClient`, `LivroClient`, `EmprestimoClient`)
- Java 21 (JavaFX 21)

Telas disponíveis: login, cadastro, tela inicial, listagem de livros, listagem de empréstimos e menu lateral de navegação.

## Como executar

### Pré-requisitos

- JDK 17 (para a API) e JDK 21 (para o desktop)
- Maven
- MySQL

### Biblioteca-api

```bash
cd Biblioteca-api
./mvnw spring-boot:run
```

A documentação da API fica disponível em `/swagger-ui.html` após iniciar a aplicação.

### Biblioteca-desktop

Com a API em execução, inicie o cliente desktop:

```bash
cd Biblioteca-desktop
./mvnw javafx:run
```

## Status do projeto

Em desenvolvimento.
