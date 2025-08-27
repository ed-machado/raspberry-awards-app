.PHONY: help build test run clean install

help:
	@echo "Golden Raspberry Awards API - Comandos:"
	@echo "  make install    - Instala dependências"
	@echo "  make build      - Compila o projeto"
	@echo "  make test       - Executa testes de integração"
	@echo "  make run        - Executa a aplicação"
	@echo "  make clean      - Limpa arquivos gerados"

default: help

install:
	./mvnw clean install -DskipTests

build:
	./mvnw clean compile

clean:
	./mvnw clean

test:
	./mvnw test

run:
	@echo "Aplicação disponível em: http://localhost:8080"
	@echo "Swagger UI: http://localhost:8080/swagger-ui.html"
	@echo "H2 Console: http://localhost:8080/h2-console"
	./mvnw spring-boot:run
