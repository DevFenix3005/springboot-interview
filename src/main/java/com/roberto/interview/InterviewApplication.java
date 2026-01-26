package com.roberto.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({ LiquibaseProperties.class })
public class InterviewApplication {

  public static void main(String[] args) {
    SpringApplication.run(InterviewApplication.class, args);
  }

}
