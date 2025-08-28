package com.goldenraspberry;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** Teste de inicializacao da aplicacao. */
@SpringBootTest
@ActiveProfiles("test")
class GoldenRaspberryAwardsApiApplicationTest {

  @Test
  void contextLoads() {
    // Validando se o contexto da aplicacao carrega sem erros
  }
}
