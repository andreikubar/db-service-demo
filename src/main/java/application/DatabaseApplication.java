package application;

import controller.DatabaseController;
import model.Database;
import model.DatabaseImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DatabaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(DatabaseApplication.class, args);
    }

    @Bean
    Database database(JdbcTemplate jdbcTemplate){
        return new DatabaseImpl(jdbcTemplate);
    }

    @Bean
    public DatabaseController databaseController(Database database){
        return new DatabaseController(database);
    }
}


