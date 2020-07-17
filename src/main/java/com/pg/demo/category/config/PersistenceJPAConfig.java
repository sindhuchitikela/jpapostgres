package com.pg.demo.category.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"com.pg.demo"})
@EnableTransactionManagement
public class PersistenceJPAConfig {
  @Value("${pg.db.driver}")
  private String driver;

  @Value("${pg.db.password}")
  private String pwd;

  @Value("${pg.db.url}")
  private String url;

  @Value("${pg.db.username}")
  private String username;

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driver);
    dataSource.setUrl(url);
    Properties dataSourceProps = new Properties();
    dataSourceProps.put("ssl", "true");
    dataSourceProps.put("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
    dataSource.setConnectionProperties(dataSourceProps);
    dataSource.setUsername(username);
    dataSource.setPassword(pwd);
    return dataSource;
  }
}