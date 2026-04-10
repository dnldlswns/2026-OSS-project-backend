package oss.backend.others;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class H2ConnectionTest {

        @Autowired
        DataSource dataSource;

        @Test
        @DisplayName("H2 DB 연결 확인")
        void connect_h2_success() throws Exception {
                Connection connection = dataSource.getConnection();

                assertNotNull(connection);
                assertNotNull(connection.getMetaData());
                assertNotNull(connection.getMetaData().getURL());

                connection.close();
        }
}