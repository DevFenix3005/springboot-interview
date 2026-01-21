package com.roberto.interview.configuration;

import static java.net.URLDecoder.decode;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.web.server.*;
import org.springframework.boot.web.server.servlet.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Slf4j
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {
  private final Environment env;

  public WebConfigurer(Environment env) {
    this.env = env;
  }

  @Override
  public void onStartup(@NonNull ServletContext servletContext) {
    if (env.getActiveProfiles().length != 0) {
      log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
    }
    log.info("Web application fully configured");
  }

  /**
   * Customize the Servlet engine: Mime types, the document root, the cache.
   */
  @Override
  public void customize(WebServerFactory server) {
    log.info("Customizing Web application configuration");
    setLocationForStaticAssets(server);
  }

  private void setLocationForStaticAssets(WebServerFactory server) {
    if (server instanceof ConfigurableServletWebServerFactory servletWebServer) {
      File root;
      String prefixPath = resolvePathPrefix();
      root = Path.of(prefixPath + "target/classes/static/").toFile();
      if (root.exists() && root.isDirectory()) {
        servletWebServer.setDocumentRoot(root);
      }
    }
  }

  private String resolvePathPrefix() {
    String fullExecutablePath = decode(this.getClass().getResource("").getPath(), StandardCharsets.UTF_8);
    String rootPath = Path.of(".").toUri().normalize().getPath();
    String extractedPath = fullExecutablePath.replace(rootPath, "");
    int extractionEndIndex = extractedPath.indexOf("target/");
    if (extractionEndIndex <= 0) {
      return "";
    }
    return extractedPath.substring(0, extractionEndIndex);
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    final CorsConfiguration config = getCorsConfiguration();

    if (!CollectionUtils.isEmpty(config.getAllowedOrigins()) || !CollectionUtils.isEmpty(config.getAllowedOriginPatterns())) {
      log.debug("Registering CORS filter");
      source.registerCorsConfiguration("/api/**", config);
      source.registerCorsConfiguration("/management/**", config);
      source.registerCorsConfiguration("/v3/api-docs", config);
      source.registerCorsConfiguration("/swagger-ui/**", config);
    }
    return new CorsFilter(source);
  }

  private static @NonNull CorsConfiguration getCorsConfiguration() {
    final String allowedOrigins =
      "http://localhost:4200,https://localhost:8100,http://localhost:9000,https://localhost:9000,http://localhost:9060,https://localhost:9060";

    final CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    config.setAllowCredentials(true);
    config.setMaxAge(1800L);
    return config;
  }

}
