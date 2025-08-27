package com.goldenraspberry.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de conectividade com o banco H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class H2DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldConnectToH2Database() throws SQLException {
        assertNotNull(dataSource, "DataSource deve estar configurado");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Conexao com H2 deve ser estabelecida");
            assertFalse(connection.isClosed(), "Conexao deve estar ativa");

            String url = connection.getMetaData().getURL();
            assertTrue(url.contains("h2"), "URL deve conter referencia ao H2");
            assertTrue(url.contains("mem"), "Deve usar H2 em memoria");
        }
    }
}
