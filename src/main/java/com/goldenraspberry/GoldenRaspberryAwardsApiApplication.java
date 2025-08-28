package com.goldenraspberry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Classe principal da aplicacao Golden Raspberry Awards API. API RESTful para consulta de
 * produtores com maior e menor intervalo entre premios.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.goldenraspberry.infrastructure.persistence.entity")
public class GoldenRaspberryAwardsApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(GoldenRaspberryAwardsApiApplication.class, args);
  }
}
