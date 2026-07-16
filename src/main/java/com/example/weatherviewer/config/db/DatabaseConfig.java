package com.example.weatherviewer.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.example.weatherviewer")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class DatabaseConfig {

    private final Environment environment;

    public DatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("db.url"));
        config.setUsername(environment.getProperty("db.username"));
        config.setPassword(environment.getProperty("db.password"));
        config.setDriverClassName(environment.getProperty("db.driver"));

        config.setMaximumPoolSize(
                environment.getProperty("db.pool.maximumPoolSize", Integer.class, 10)
        );
        config.setMinimumIdle(
                environment.getProperty("db.pool.minimumIdle", Integer.class, 5)
        );
        config.setConnectionTimeout(
                environment.getProperty("db.pool.connectionTimeout", Long.class, 30000L)
        );
        config.setIdleTimeout(
                environment.getProperty("db.pool.idleTimeout", Long.class, 600000L)
        );
        config.setMaxLifetime(
                environment.getProperty("db.pool.maxLifetime", Long.class, 1800000L)
        );

        return new HikariDataSource(config);
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.example.weatherviewer.entity");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new HibernateTransactionManager(Objects.requireNonNull(sessionFactory().getObject()));
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        props.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql", "false"));
        props.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto", "none"));
        return props;
    }
}