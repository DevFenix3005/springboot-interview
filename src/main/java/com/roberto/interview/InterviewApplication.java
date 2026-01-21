package com.roberto.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;

@SpringBootApplication
@EnableConfigurationProperties({ LiquibaseProperties.class })
public class InterviewApplication {

  static void main(String[] args) {
    SpringApplication.run(InterviewApplication.class, args);
  }

}
