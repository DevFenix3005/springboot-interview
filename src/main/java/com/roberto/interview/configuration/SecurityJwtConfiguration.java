package com.roberto.interview.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import com.roberto.interview.management.SecurityMetersService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityJwtConfiguration {

  @Value("${security.jwt.key}")
  private String jwtKey;

  @Bean
  public JwtDecoder jwtDecoder(final SecurityMetersService securityMetersService) {
    final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(AppConstants.JWT_ALGORITHM).build();
    return token -> {
      try {
        return jwtDecoder.decode(token);
      } catch (Exception e) {
        if (e.getMessage().contains("Invalid signature")) {
          securityMetersService.trackTokenInvalidSignature();
        } else if (e.getMessage().contains("Jwt expired at")) {
          securityMetersService.trackTokenExpired();
        } else if (
          e.getMessage().contains("Invalid JWT serialization") ||
            e.getMessage().contains("Malformed token") ||
            e.getMessage().contains("Invalid unsecured/JWS/JWE")
        ) {
          securityMetersService.trackTokenMalformed();
        } else {
          log.error("Unknown JWT error {}", e.getMessage());
        }
        throw e;
      }
    };
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
  }

  private SecretKey getSecretKey() {
    byte[] keyBytes = Base64.from(jwtKey).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, AppConstants.JWT_ALGORITHM.getName());
  }

}
