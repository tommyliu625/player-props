package com.player.props.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

@Component
public class EntityManagerConfig {

  private HikariDataSource dataSource;

  EntityManagerConfig(HikariDataSource dataSource) {
    this.dataSource = dataSource;
  }

  // @Bean
  // public EntityManagerFactory cFactory () {
  //   Map<String, Object> properties = new HashMap<>();
  //   properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
  //   properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost/tommyliu");
  //   properties.put("javax.persistence.jdbc.user", "tommyliu");
  //   properties.put("javax.persistence.jdbc.password", "");
  //   properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
  //   // properties.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
  //   properties.put("hibernate.hikari.dataSource", dataSource);

  //   EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPerstientUnit", properties);

  //   return entityManagerFactory;
  // }

  @Bean
  public EntityManagerFactory createEntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

    em.setDataSource(dataSource);
    em.setPersistenceUnitName("persistenceUnitForPredicate");
    em.setPackagesToScan("com.player.props.dao");
    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    em.setJpaPropertyMap(noVerbos());
    em.afterPropertiesSet();
    return em.getObject();
  }


  private Map<String, Object> noVerbos() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("hibernate.show_sql", "false");
    properties.put("hibernate.formal_sql", "false");
    properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    return properties;
  }

  // @Bean
  // public EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
  //   return entityManagerFactory.createEntityManager();
  // }
  // EntityManagerConfig(HikariDataSource dataSource) {

  //   Map<String, Object> properties = new HashMap<>();
  //   properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
  //   properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost/tommyliu");
  //   properties.put("javax.persistence.jdbc.user", "tommyliu");
  //   properties.put("javax.persistence.jdbc.password", "");
  //   properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
  //   properties.put("hibernate.hikari.dataSource", dataSource);

  //   EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit", properties);
  //   this.entityManager = entityManagerFactory.createEntityManager();
  // }

}
