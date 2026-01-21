package com.roberto.interview.configuration;

import java.util.Optional;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.liquibase.autoconfigure.DataSourceClosingSpringLiquibase;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseDataSource;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LiquibaseConfiguration {

  @Bean
  public SpringLiquibase springLiquibase(
    final LiquibaseProperties liquibaseProperties,
    @LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource,
    ObjectProvider<DataSource> dataSources,
    DataSourceProperties dataSourceProperties
  ) {

    log.info("Liquibase configuration has been initialized");

    final SpringLiquibase liquibase;
    final DataSource liquibaseDS = getDataSource(liquibaseDataSource.getIfAvailable(), liquibaseProperties, dataSources.getIfUnique());
    if (liquibaseDS != null) {
      liquibase = new SpringLiquibase();
      liquibase.setDataSource(liquibaseDS);
    } else {
      liquibase = new DataSourceClosingSpringLiquibase();
      liquibase.setDataSource(createNewDataSource(liquibaseProperties, dataSourceProperties));
    }
    liquibase.setChangeLog(liquibaseProperties.getChangeLog());
    liquibase.setDropFirst(liquibaseProperties.isDropFirst());
    if (!CollectionUtils.isEmpty(liquibaseProperties.getContexts())) {
      liquibase.setContexts(StringUtils.collectionToCommaDelimitedString(liquibaseProperties.getContexts()));
    }
    return liquibase;
  }

  private DataSource getDataSource(
    DataSource liquibaseDataSource,
    LiquibaseProperties liquibaseProperties,
    DataSource dataSource
  ) {
    if (liquibaseDataSource != null) {
      return liquibaseDataSource;
    }
    if (liquibaseProperties.getUrl() == null && liquibaseProperties.getUser() == null) {
      return dataSource;
    }
    return null;
  }

  private DataSource createNewDataSource(LiquibaseProperties liquibaseProperties, DataSourceProperties dataSourceProperties) {
    String url = getProperty(liquibaseProperties::getUrl, dataSourceProperties::determineUrl);
    String user = getProperty(liquibaseProperties::getUser, dataSourceProperties::determineUsername);
    String password = getProperty(liquibaseProperties::getPassword, dataSourceProperties::determinePassword);
    return DataSourceBuilder.create().url(url).username(user).password(password).build();
  }

  private String getProperty(Supplier<String> property, Supplier<String> defaultValue) {
    return Optional.of(property).map(Supplier::get).orElseGet(defaultValue);
  }

}
