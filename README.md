# Golden Raspberry Awards API

API RESTful para consulta de produtores com maior e menor intervalo entre prêmios do Golden Raspberry Awards.

## 📋 Sobre o Projeto

Esta aplicação foi desenvolvida para fornecer informações sobre os filmes indicados ao Golden Raspberry Awards, com foco especial na análise dos intervalos entre prêmios dos produtores.

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

### Domínio
- **Entidades**: Movie, Producer, Year, ProducerInterval
- **Serviços de Domínio**: ProducerIntervalCalculator
- **Repositórios**: MovieRepository, ProducerIntervalService
- **Validações**: Regras de negócio para filmes e produtores

### Casos de Uso
- **GetAllMoviesUseCase**: Obter todos os filmes
- **GetWinnerMoviesUseCase**: Obter filmes vencedores
- **GetProducerIntervalsUseCase**: Calcular intervalos entre prêmios dos produtores

### Serviços de Aplicação
- **MovieApplicationService**: Orquestração dos casos de uso
- **GlobalExceptionHandler**: Tratamento centralizado de exceções

### Infraestrutura
- **InMemoryMovieRepository**: Implementação em memória do repositório
- **ProducerIntervalServiceImpl**: Implementação do serviço de intervalos
- **BeanConfiguration**: Configuração de injeção de dependências

## 🧪 Testes

O projeto possui cobertura abrangente de testes:

- **Testes de Domínio**: Validação das regras de negócio
- **Testes de Casos de Uso**: Verificação da lógica de aplicação
- **Testes de Serviços**: Validação da orquestração
- **Testes de Integração**: Verificação do contexto completo da aplicação

## 📊 Estrutura do Projeto

```
src/
├── main/java/com/goldenraspberry/
│   ├── application/          # Casos de uso e serviços de aplicação
│   ├── common/              # Anotações e exceções compartilhadas
│   ├── domain/              # Entidades e regras de negócio
│   └── infrastructure/      # Implementações e configurações
└── test/java/com/goldenraspberry/
    ├── application/         # Testes de casos de uso e serviços
    ├── domain/             # Testes de entidades e serviços de domínio
    └── infrastructure/     # Testes de infraestrutura
```

## ⚙️ Configuração

A aplicação utiliza:
- **Banco H2** em memória para desenvolvimento
- **Porta 8080** como padrão
- **Processamento de CSV** com separador `;`
- **Documentação OpenAPI** disponível via Swagger UI

## 🔧 Comandos Disponíveis

```bash
# Instalar dependências
make install

# Compilar o projeto
make build

# Executar testes
make test

# Limpar arquivos gerados
make clean
```

---

**Status**: Em desenvolvimento - Camada de domínio, aplicação e infraestrutura básica implementadas com testes completos.
