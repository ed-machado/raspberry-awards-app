package com.goldenraspberry.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuração principal de beans e injecao de dependencias.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.goldenraspberry.infrastructure.persistence")
@ComponentScan(basePackages = {
    "com.goldenraspberry.domain",
    "com.goldenraspberry.application",
    "com.goldenraspberry.infrastructure",
    "com.goldenraspberry.common"
})
public class BeanConfiguration {

    // TODO
}
