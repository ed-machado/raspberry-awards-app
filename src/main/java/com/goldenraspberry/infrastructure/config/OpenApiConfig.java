package com.goldenraspberry.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao do OpenAPI/Swagger para documentacao da API Implementa documentacao automatica dos
 * endpoints REST
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Golden Raspberry Awards API")
                .version("1.0.0")
                .description(
                    "API REST para consulta de dados do Golden Raspberry Awards (Pior Filme)")
                .contact(
                    new Contact()
                        .name("Golden Raspberry Awards")
                        .email("contact@goldenraspberry.com")
                        .url("https://github.com/goldenraspberry/api"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor de Desenvolvimento"),
                new Server()
                    .url("https://api.goldenraspberry.com")
                    .description("Servidor de Produção")));
  }
}
