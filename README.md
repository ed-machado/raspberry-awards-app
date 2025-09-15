# Golden Raspberry Awards API

API RESTful para consulta de produtores com maior e menor intervalo entre prêmios do Golden Raspberry Awards.

## 📋 Sobre o Projeto

Aplicacao desenvolvida para fornecer informações sobre os filmes indicados ao Golden Raspberry Awards, com foco especial na análise dos intervalos entre prêmios.

## 🏗️ Arquitetura

O projeto segue os princípios da **Arquitetura Hexagonal (Ports and Adapters)** com **Domain-Driven Design (DDD)**:

- **Domain**: Contém as regras de negócio, entidades e serviços de domínio
- **Application**: Casos de uso e orquestração de serviços
- **Infrastructure**: Implementações de repositórios, configurações e adaptadores externos
- **Common**: Anotações, exceções e utilitários compartilhados

## 🚀 Tecnologias Utilizadas

- **Java 21** com Preview Features
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (em memória)
- **OpenCSV** para processamento de arquivos CSV
- **SpringDoc OpenAPI** para documentação da API
- **JUnit 5** para testes
- **Maven** para gerenciamento de dependências

## 📦 Funcionalidades Implementadas

### Carregamento de Dados CSV
- **Carregamento Automático**: Todos os arquivos CSV em `src/main/resources/` são carregados automaticamente na inicialização
- **Múltiplos Arquivos**: Suporte para múltiplos arquivos CSV (movielist.csv, movielist2.csv, etc.)
- **Endpoint de Upload**: `POST /api/v1/movies/upload-csv` para carregar novos arquivos CSV
- **Persistência JPA**: Dados salvos em banco H2 em memória

### Domínio
- **Entidades**: Movie, Producer, Year, ProducerInterval
- **Serviços de Domínio**: ProducerIntervalCalculator
- **Ports (Interfaces)**: MovieRepository, ProducerIntervalService
- **Validações**: Regras de negócio para filmes e produtores

### Casos de Uso
- **CreateMovieUseCase**: Criar novo filme
- **DeleteMovieUseCase**: Deletar filme existente
- **GetAllMoviesUseCase**: Obter todos os filmes
- **GetMovieByIdUseCase**: Obter filme por ID
- **GetProducerIntervalsUseCase**: Calcular intervalos entre prêmios dos produtores
- **GetWinnerMoviesUseCase**: Obter filmes vencedores
- **UpdateMovieUseCase**: Atualizar filme existente

### Serviços de Aplicação
- **MovieApplicationService**: Orquestração dos casos de uso
- **GlobalExceptionHandler**: Tratamento centralizado de exceções

### Infraestrutura
- **MovieJpaRepository**: Implementação JPA com Spring Data
- **MovieCsvLoader**: Carregador de arquivos CSV
- **DataInitializer**: Inicializador automático de dados
- **ProducerIntervalServiceImpl**: Implementação do serviço de intervalos

## 🧪 Testes

- **Testes de Integração Completos**: Todos os testes utilizam `@SpringBootTest` e carregam o contexto completo da aplicação
- **Cobertura Abrangente**: Validação de endpoints REST, persistência JPA, regras de negócio e casos de uso
- **Dados Reais**: Testes executam com dados carregados do CSV e validam comportamentos end-to-end
- **Transacionais**: Testes que modificam dados utilizam `@Transactional` para isolamento

## 📊 Estrutura do Projeto

```
src/
├── main/java/com/goldenraspberry/
│   ├── application/         # Casos de uso e serviços de aplicação
│   ├── common/              # Anotações e exceções compartilhadas
│   ├── domain/              # Entidades e regras de negócio
│   └── infrastructure/      # Implementações e configurações
└── test/java/com/goldenraspberry/
    └── infrastructure/      # Testes de infraestrutura
```

## 🌐 Endpoints da API

### 🎯 Endpoint Principal
```http
GET /api/v1/producers/intervals
```
**Descrição**: Retorna o produtor com maior intervalo entre dois prêmios consecutivos e o que obteve dois prêmios mais rápido.

**Exemplo de Resposta**:
```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

### 📊 Consulta de Dados
- `GET /api/v1/movies` - Listar todos os filmes carregados
- `GET /api/v1/movies/winners` - Listar apenas filmes vencedores
- `GET /api/v1/movies/{id}` - Buscar filme específico por ID

### ⚙️ Operações CRUD (Extras)
- `POST /api/v1/movies` - Criar novo filme
- `PUT /api/v1/movies/{id}` - Atualizar filme existente
- `DELETE /api/v1/movies/{id}` - Deletar filme

### 🔍 Monitoramento e Documentação
- `GET /api/v1/health` - Status da aplicação
- **Swagger UI**: http://localhost:8080/docs - Documentação interativa completa
- **H2 Console**: http://localhost:8080/h2-console - Interface do banco de dados

## 📁 Dados da Aplicação

### Carregamento Automático
A aplicação carrega automaticamente os dados do arquivo `movielist.csv` localizado em `src/main/resources/` durante a inicialização.

### Formato do CSV
O arquivo utiliza o formato:
```
year;title;studios;producers;winner
1980;Can't Stop the Music;Associated Film Distribution;Allan Carr;yes
```

## 🚀 Como Executar

### Pré-requisitos
- **Java 21+** (com suporte a Preview Features)
- **Maven 3.8+**
- **Git** para clonar o repositório

### 📋 Guia Completo de Setup

#### 1. Clone do Repositório
```bash
# Clone o projeto
git clone <URL_DO_REPOSITORIO>
cd raspberry-awards-app
```

#### 2. Setup e Instalação de Dependências
```bash
# Instalar todas as dependências do projeto
make install

# Ou manualmente:
mvn clean install -DskipTests
```

#### 3. Build do Projeto
```bash
# Compilar o projeto
make build

# Ou manualmente:
mvn clean compile
```

#### 4. Executar Testes de Integração
```bash
# Executar todos os testes de integração
make test

# Ou manualmente:
mvn test
```

#### 5. Executar a Aplicação
```bash
# Iniciar a aplicação
make run

# Ou manualmente:
mvn spring-boot:run
```

#### 6. Acessar a Aplicação
Após executar, a aplicação estará disponível em:
- **API Base**: http://localhost:8080/api/v1
- **Documentação Swagger**: http://localhost:8080/docs
- **Console H2**: http://localhost:8080/h2-console

### Acesso ao Banco H2
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:goldenraspberry`
- **Usuário**: `sa`
- **Senha**: (vazia)
- **Driver**: `org.h2.Driver`

### 🧪 Testando a API
```bash
# Testar endpoint principal
curl http://localhost:8080/api/v1/producers/intervals

# Verificar health da aplicação
curl http://localhost:8080/api/v1/health

# Listar todos os filmes
curl http://localhost:8080/api/v1/movies
```

## Testes

- **Testes de Integração Completos**: Todos os testes utilizam `@SpringBootTest` e carregam o contexto completo da aplicação
- **Cobertura Abrangente**: Validação de endpoints REST, persistência JPA, regras de negócio e casos de uso
- **Dados Reais**: Testes executam com dados carregados do CSV e validam comportamentos end-to-end
- **Transacionais**: Testes que modificam dados utilizam `@Transactional` para isolamento
