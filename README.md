# 🛒 Kora — Loja Inteligente

**Nome do projeto:** Kora — Loja Inteligente  
**Integrantes:** Vitória Moreno Tomazeli e Michel Amorim

## 1. Repositórios

- **Backend:** https://github.com/vitomazeli/loja-inteligente-backend.git
- **Frontend:** https://github.com/vitomazeli/loja-inteligente-frontend.git

## 2. Integrantes e responsabilidades

| Integrante | Entidades sob sua responsabilidade |
|---|---|
| Vitória Moreno Tomazeli | Categorias e Produtos |
| Michel Amorim | Vendas e Clientes |

## 3. Descrição do projeto

O **Kora — Loja Inteligente** é uma aplicação desenvolvida para auxiliar no gerenciamento das operações de uma loja, centralizando informações sobre categorias, produtos, clientes e vendas.

A solução permite realizar operações de cadastro, consulta, atualização e exclusão de registros, por meio das funcionalidades de CRUD (*Create, Read, Update e Delete*).

O objetivo é facilitar a organização das informações comerciais, reduzir a dependência de controles manuais e tornar o gerenciamento dos dados mais eficiente.

Entre as principais funcionalidades estão:

- **Gerenciamento de categorias:** cadastro, consulta, atualização e exclusão de categorias de produtos.
- **Gerenciamento de produtos:** manutenção dos produtos e associação com suas respectivas categorias.
- **Gerenciamento de clientes:** cadastro e gerenciamento das informações dos clientes.
- **Gerenciamento de vendas:** registro e consulta das vendas realizadas.
- **Persistência de dados:** armazenamento das informações em banco de dados.
- **Interface gráfica:** interação com as funcionalidades do sistema por meio de uma interface desenvolvida com Compose Multiplatform.

A aplicação é dividida em frontend e backend, separando a interface utilizada pelo usuário das regras de negócio e do acesso aos dados. Essa organização facilita a manutenção e a evolução do sistema.

## 4. Tecnologias utilizadas

### Backend

- Kotlin
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- SQLite
- Jakarta Validation
- Spring Security
- Gradle
- Postman para testes da API

### Frontend

- Kotlin
- Compose Multiplatform
- Material 3
- Gradle

## 5. Arquitetura e organização dos diretórios

### 5.1. Backend

O backend utiliza uma arquitetura em camadas, separando as responsabilidades de recebimento das requisições, processamento das regras de negócio e acesso aos dados.

Estrutura principal:

```text
src/
└── main/
    ├── kotlin/
    │   └── br/com/lojainteligente/
    │       ├── categoria/
    │       ├── cliente/
    │       ├── produto/
    │       ├── venda/
    │       ├── SecurityConfig.kt
    │       └── GlobalExceptionHandler.kt
    └── resources/
        └── application.properties
```

Os diretórios de cada entidade agrupam os arquivos relacionados às suas funcionalidades.

| Camada | Responsabilidade |
|---|---|
| Controller | Receber requisições HTTP e retornar respostas da API. |
| Service | Implementar as regras de negócio. |
| Repository | Realizar operações de acesso e persistência dos dados. |
| Entity | Representar as entidades armazenadas no banco de dados. |
| DTO | Definir os dados recebidos e retornados pela API. |
| Mapper | Converter dados entre entidades e DTOs. |
| Configuração | Centralizar configurações de segurança e tratamento global de exceções. |

O fluxo principal de uma requisição é:

```text
Cliente HTTP
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
JPA / Hibernate
     ↓
SQLite
```

O arquivo `application.properties`, localizado em `src/main/resources/`, contém configurações da aplicação, como parâmetros de conexão com o banco de dados e a porta do servidor.

### 5.2. Frontend

O frontend utiliza Kotlin e Compose Multiplatform, com código compartilhado entre os módulos da aplicação.

Estrutura principal:

```text
shared/
└── src/
    └── commonMain/
        └── kotlin/
            └── br/com/lojainteligente/frontend/
                ├── ui/
                ├── model/
                └── data/
```

| Diretório | Responsabilidade |
|---|---|
| `ui/` | Telas, formulários, listas e componentes visuais. |
| `model/` | Modelos de dados utilizados pelo frontend. |
| `data/` | Comunicação com a API backend e serviços de acesso aos dados. |

O projeto possui os seguintes módulos:

- `androidApp/`: módulo da aplicação Android.
- `desktopApp/`: módulo da aplicação desktop.
- `webApp/`: módulo da aplicação web.
- `shared/`: código compartilhado entre as plataformas.

O frontend se comunica com o backend por meio de requisições HTTP, utilizando a API REST para consultar e modificar os dados.

## 6. Instalação, execução e acesso

### 6.1. Pré-requisitos

Para executar o projeto, são necessários:

- Git.
- JDK 21.
- IntelliJ IDEA ou IDE compatível com Kotlin e Gradle.
- Postman ou Insomnia para testes manuais da API.
- Android SDK, caso seja utilizada a versão Android.

### 6.2. Clonar os repositórios

Clone o backend:

```bash
git clone https://github.com/vitomazeli/loja-inteligente-backend.git
```

Clone o frontend em outro diretório:


```bash
git clone https://github.com/vitomazeli/loja-inteligente-frontend.git
```



### 6.3. Executar o backend

No diretório do backend, execute:

**Windows:**

```powershell
.\gradlew.bat bootRun
```

**Linux/macOS:**

```bash
./gradlew bootRun
```

O backend utiliza a porta `8082` na configuração atual do projeto.

Endereço-base da API:

```text
http://localhost:8082
```

O banco de dados SQLite é configurado pela aplicação. Consulte o arquivo `application.properties` para verificar os parâmetros de conexão.

### 6.4. Testar a API

Com o backend em execução, utilize o Postman ou Insomnia para enviar requisições HTTP.

Exemplos de endpoints:

| Operação | Método | Endpoint |
|---|---|---|
| Listar categorias | GET | `/api/categorias` |
| Buscar categoria por ID | GET | `/api/categorias/{id}` |
| Cadastrar categoria | POST | `/api/categorias` |
| Atualizar categoria | PUT | `/api/categorias/{id}` |
| Excluir categoria | DELETE | `/api/categorias/{id}` |
| Listar clientes | GET | `/api/clientes` |
| Listar produtos | GET | `/api/produtos` |
| Listar vendas | GET | `/api/vendas` |

Exemplo de endereço completo:

```text
http://localhost:8082/api/categorias
```

Para operações de cadastro e atualização, envie os dados no corpo da requisição em formato JSON, conforme os DTOs definidos pela API.

### 6.5. Executar o frontend

No terminal, acesse a pasta raiz do frontend.

Para executar a versão desktop no Windows:

```powershell
.\gradlew.bat :desktopApp:run
```

O Gradle iniciará a aplicação gráfica.

Ao iniciar a aplicação utilize as seguintes credenciais de login:

email: teste.jwt@loja.com
senha: Senha123

Para executar as versões Android ou web, utilize as tarefas correspondentes aos módulos `androidApp` e `webApp`.

**Importante:** mantenha o backend em execução enquanto o frontend precisar consultar ou modificar dados. A URL configurada no frontend deve apontar para o endereço correto da API.
