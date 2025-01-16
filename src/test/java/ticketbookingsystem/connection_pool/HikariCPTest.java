package ticketbookingsystem.connection_pool;

import org.junit.jupiter.api.Test;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.zaxxer.hikari.HikariConfig;

public class HikariCPTest {

    @Test
    public void testConnectionPool() {

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:ticket-booking-system-test");

        Configuration configuration = new Configuration();

        configuration.configure();

        configuration.setProperty("hibernate.connection.provider_class",
                                  "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        configuration.setProperty("hibernate.hikari.dataSource.url", hikariConfig.getJdbcUrl());

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        // Имитируем нагрузку
        for (int i = 0; i < 50; i++) {
            sessionFactory.openSession().doWork(connection -> {
                connection.createStatement().execute("SELECT 1");
            });
        }
    }
}