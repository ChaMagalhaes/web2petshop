# Web2Petshop

Sistema web de gerenciamento de pets e agendamentos para petshop, desenvolvido como projeto acadêmico da disciplina **Desenvolvimento Web II**.

O projeto tem como objetivo aplicar conceitos de desenvolvimento web com Java e Spring Boot, trabalhando organização em camadas, persistência relacional, autenticação e autorização, validação de dados, integração com banco de dados e testes automatizados.

> **Status:** Concluido.

---

## Visão geral

O Web2Petshop permite organizar informações de pets e seus agendamentos em uma aplicação web. A aplicação possui páginas renderizadas no servidor com Thymeleaf e também disponibiliza uma API REST para operações relacionadas a pets.

### Funcionalidades implementadas

- **Cadastro e consulta de pets**
    - Listagem de pets.
    - Cadastro de novos pets.
    - Edição de dados.
    - Exclusão de registros.
    - Validação de campos como nome, raça, idade e tamanho.

- **Gerenciamento de agendamentos**
    - Listagem de agendamentos.
    - Criação e edição de agendamentos.
    - Associação de um agendamento a um pet.
    - Registro de um ou mais serviços no agendamento.
    - Cálculo do valor total a partir dos serviços.
    - Validação de data, status, pet e serviços.

- **Usuários e segurança**
    - Páginas de login e cadastro de usuário.
    - Autenticação com Spring Security.
    - Senhas codificadas com BCrypt.
    - Controle de acesso por autoridade `ADMIN`.
    - Página de acesso negado.

- **API REST de pets**
    - Consulta de todos os pets.
    - Consulta de pet por ID.
    - Criação, atualização e exclusão de pets.
    - Respostas HTTP apropriadas para operações realizadas e recursos não encontrados.
    - Validação dos dados recebidos.

---

## Tecnologias e ferramentas

| Tecnologia / ferramenta | Utilização |
|---|---|
| **Java 25** | Linguagem principal da aplicação. |
| **Spring Boot 4.1.2** | Base da aplicação e configuração dos componentes. |
| **Spring MVC** | Controladores e tratamento das requisições web. |
| **Spring Data JPA** | Persistência e acesso aos dados. |
| **Hibernate** | Implementação ORM utilizada pelo JPA. |
| **PostgreSQL** | Banco de dados da aplicação. |
| **H2 Database** | Banco em memória utilizado nos testes automatizados. |
| **Thymeleaf** | Renderização das páginas HTML no servidor. |
| **Spring Security** | Autenticação, autorização e proteção das rotas. |
| **Bean Validation** | Validação dos dados das entidades e formulários. |
| **Bootstrap 4.1.3** | Componentes e estilos da interface. |
| **HTML5 / CSS3** | Estrutura e estilização das páginas. |
| **JavaScript** | Recursos de interação no front-end. |
| **Maven** | Gerenciamento de dependências e build. |
| **Maven Wrapper** | Execução do Maven sem exigir uma instalação global. |
| **JUnit 5 / Spring Test / MockMvc** | Testes automatizados da aplicação. |
| **Mockito** | Simulação de dependências nos testes unitários. |
| **SpringDoc OpenAPI** | Documentação da API e interface Swagger UI. |
| **Lombok** | Redução de código repetitivo, como getters e setters. |
| **Git e GitHub** | Versionamento e hospedagem do código-fonte. |
| **IntelliJ IDEA** | Ambiente de desenvolvimento utilizado. |

---

## Arquitetura e organização do código

A aplicação segue uma organização em camadas, separando responsabilidades para facilitar manutenção, testes e evolução.

```text
src/
├── main/
│   ├── java/io/github/chamagalhaes/web2petshop/
│   │   ├── config/        # Configurações gerais e segurança
│   │   ├── controller/    # Controladores MVC e API REST
│   │   ├── impl/          # Implementações dos serviços
│   │   ├── model/         # Entidades do domínio
│   │   ├── repository/    # Acesso e persistência de dados
│   │   ├── service/       # Contratos dos serviços
│   │   └── Web2petshopApplication.java
│   │
│   └── resources/
│       ├── static/
│       │   └── css/       # Arquivos CSS
│       ├── templates/     # Templates Thymeleaf
│       │   ├── agendamento/
│       │   ├── error/
│       │   ├── fragments/
│       │   ├── pet/
│       │   └── user/
│       └── application.properties
│
└── test/
    ├── java/              # Testes automatizados
    └── resources/
        └── application-test.properties
```

### Responsabilidade das camadas

- **Controller:** recebe requisições, prepara os dados necessários para as páginas e direciona as operações para os serviços.
- **Service:** concentra as regras de negócio e coordena as operações da aplicação.
- **Repository:** abstrai o acesso ao banco de dados por meio do Spring Data JPA.
- **Model:** representa as entidades e seus relacionamentos.
- **Config:** reúne configurações transversais, como segurança e codificação de senhas.
- **Templates:** contém as páginas HTML renderizadas pelo Thymeleaf.

A injeção de dependências é feita pelos construtores das classes, favorecendo baixo acoplamento e facilitando os testes.

---

## Modelo de domínio

As principais entidades identificadas no projeto são:

| Entidade | Responsabilidade |
|---|---|
| `Pet` | Armazena nome, raça, idade e tamanho do pet. |
| `Agendamento` | Armazena data e hora, status, pet associado e valor total. |
| `AgendamentoServico` | Representa cada serviço incluído em um agendamento, com nome e valor. |
| `User` | Armazena dados de acesso e informações do usuário. |

### Relacionamentos

- Um **pet** pode possuir vários agendamentos.
- Cada **agendamento** está associado a um pet.
- Um **agendamento** pode possuir vários serviços.
- Cada registro de **AgendamentoServico** pertence a um agendamento.
- Um **usuário** pode possuir uma ou mais autoridades armazenadas na coleção `roles`.

O valor total do agendamento é calculado a partir da soma dos valores dos serviços associados.

---

## Segurança

A segurança é configurada com Spring Security, utilizando duas cadeias de filtros:

- Rotas da API sob `/api/**`: exigem a autoridade `ADMIN` e utilizam autenticação HTTP Basic.
- Rotas da aplicação web: utilizam formulário de login.
- Operações administrativas de pets e agendamentos: restritas à autoridade `ADMIN`.
- Recursos estáticos, login e cadastro: possuem acesso público conforme a configuração.
- Senhas: codificadas com BCrypt por meio de um `PasswordEncoder`.
- CORS: configuração de origem permitida para o ambiente de desenvolvimento front-end (`http://localhost:5173`).

> A configuração de segurança deve ser revisada antes de qualquer implantação pública. Em especial, credenciais, origens CORS, CSRF e regras de acesso precisam ser ajustadas ao ambiente de produção.

---

## API REST

A API de pets está organizada sob o prefixo `/api/pet`.

| Método | Endpoint | Operação |
|---|---|---|
| `GET` | `/api/pet` | Lista todos os pets. |
| `GET` | `/api/pet/{id}` | Consulta um pet pelo ID. |
| `POST` | `/api/pet` | Cadastra um pet. |
| `PUT` | `/api/pet/{id}` | Atualiza um pet. |
| `DELETE` | `/api/pet/{id}` | Exclui um pet. |

A documentação OpenAPI/Swagger é disponibilizada pelo SpringDoc. Com a aplicação em execução, os caminhos usuais são:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Documento OpenAPI: `http://localhost:8080/v3/api-docs`

O acesso efetivo depende das configurações de segurança e da configuração da aplicação.

---

## Testes automatizados

Os testes são organizados para verificar diferentes níveis da aplicação:

- **Testes de controller:** verificam o comportamento das rotas MVC, respostas, redirecionamentos, atributos do modelo e validações.
- **Testes de service:** verificam regras de negócio e interações com os repositórios utilizando Mockito.
- **Testes de integração:** exercitam o fluxo entre componentes da aplicação e a persistência.
- **Teste de contexto:** verifica se o contexto Spring consegue iniciar.

### Resultado registrado no pacote analisado

| Classe de teste | Testes |
|---|---:|
| `AgendamentoControllerTest` | 14 |
| `PetControllerTest` | 8 |
| `AgendamentoServiceImplTest` | 7 |
| `AgendamentoIntegrationTest` | 7 |
| `PetIntegrationTest` | 1 |
| `Web2petshopApplicationTests` | 1 |
| **Total** | **38** |

**Resultado registrado:** 38 executados, 0 falhas, 0 erros e 0 ignorados.

Os testes utilizam o perfil `test` e o banco H2 em memória (`jdbc:h2:mem:testdb`), isolando a execução de testes do banco PostgreSQL utilizado no desenvolvimento.

### Executar os testes

No Windows PowerShell:

```powershell
.\mvnw clean test
```

No Linux ou macOS:

```bash
./mvnw clean test
```

---

## Configuração do ambiente

### Pré-requisitos

- JDK 25.
- PostgreSQL instalado e em execução.
- Git.
- IDE de sua preferência (o projeto foi desenvolvido utilizando IntelliJ IDEA).

O Maven Wrapper (`mvnw` / `mvnw.cmd`) está incluído no repositório e pode ser utilizado para executar os comandos Maven.

### 1. Clonar o repositório

```bash
git clone https://github.com/ChaMagalhaes/web2petshop.git
cd web2petshop
```

### 2. Criar o banco de dados

No PostgreSQL, crie o banco utilizado pela aplicação:

```sql
CREATE DATABASE bd_web2petshop;
```

### 3. Configurar a conexão

Edite `src/main/resources/application.properties` e configure as propriedades de conexão de acordo com seu ambiente:

```properties
spring.application.name=web2petshop

spring.datasource.url=jdbc:postgresql://localhost:5432/bd_web2petshop
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Importante:** não utilize credenciais reais diretamente em repositórios públicos. Para uso fora do ambiente local, prefira variáveis de ambiente ou um mecanismo seguro de gerenciamento de segredos. O arquivo de configuração versionado deve conter apenas valores apropriados para compartilhamento.

### 4. Executar a aplicação

Windows PowerShell:

```powershell
.\mvnw spring-boot:run
```

Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Após a inicialização, acesse:

```text
http://localhost:8080
```

### 5. Executar os testes

Windows:

```powershell
.\mvnw clean test
```

Linux ou macOS:

```bash
./mvnw clean test
```

---

## Ferramentas e práticas de desenvolvimento

Durante o desenvolvimento foram utilizadas e aplicadas práticas como:

- Controle de versão com Git e hospedagem no GitHub.
- Organização do código por responsabilidades e camadas.
- Injeção de dependências por construtor.
- Separação entre controllers, serviços, repositórios e entidades.
- Validação de entrada com Jakarta Bean Validation.
- Persistência e mapeamento objeto-relacional com JPA/Hibernate.
- Autenticação e autorização com Spring Security.
- Testes unitários e de integração.
- Banco H2 isolado para execução dos testes.
- Maven Wrapper para padronizar a execução do build.
- Convenção de commits baseada em Conventional Commits.


---

## Objetivo acadêmico e profissional

Este projeto foi desenvolvido para consolidar conhecimentos de desenvolvimento back-end e aplicações web utilizando Java e o ecossistema Spring.

Além da implementação das funcionalidades, o trabalho envolve decisões de arquitetura, modelagem de entidades, persistência de dados, segurança, validação, integração entre camadas e testes automatizados.

O repositório também funciona como parte do meu portfólio, demonstrando minha evolução prática no desenvolvimento de software.

---

## Autor

**Charles Magalhães**

- GitHub: [@ChaMagalhaes](https://github.com/ChaMagalhaes)
- Repositório: [web2petshop](https://github.com/ChaMagalhaes/web2petshop)

---

<p align="center">
  Desenvolvido com Java e Spring Boot.
</p>
