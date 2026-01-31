package com.roberto.interview.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.roberto.interview.security.SecurityUtils;

@EnableCaching
@Configuration(proxyBeanMethods = false)
public class CacheConfiguration {

  private static final String FIND_ALL_PREFIX = "findAll_";

  @Bean
  public KeyGenerator taskListByUserGen(SecurityUtils securityUtils) {
    return (_, _, _) -> {
      final String user = securityUtils.getCurrentUserLogin().orElse("anonymous");
      return FIND_ALL_PREFIX + user;
    };
  }

}
