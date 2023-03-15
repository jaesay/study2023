package com.tobyspring.config.autoconfig;

import com.tobyspring.config.EnableMyConfigurationProperties;
import com.tobyspring.config.MyAutoConfiguration;
import com.tobyspring.config.MyConditionalOnClass;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Driver;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@MyAutoConfiguration
@MyConditionalOnClass("org.springframework.jdbc.core.JdbcOperations")
@EnableMyConfigurationProperties(MyDataSourceProperties.class)
public class DataSourceConfig {

  @Bean
  @MyConditionalOnClass("com.zaxxer.hikari.HikariDataSource")
  @ConditionalOnMissingBean
  DataSource hikariDataSource(MyDataSourceProperties properties) throws ClassNotFoundException {
    HikariDataSource dataSource = new HikariDataSource();

    dataSource.setDriverClassName(properties.getDriverClassName());
    dataSource.setJdbcUrl(properties.getUrl());
    dataSource.setUsername(properties.getUsername());
    dataSource.setPassword(properties.getPassword());

    return dataSource;
  }

  @Bean
  @ConditionalOnMissingBean
  DataSource dataSource(MyDataSourceProperties properties) throws ClassNotFoundException {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(
        (Class<? extends Driver>) Class.forName(properties.getDriverClassName()));
    dataSource.setUrl(properties.getUrl());
    dataSource.setUsername(properties.getUsername());
    dataSource.setPassword(properties.getPassword());

    return dataSource;
  }
}
