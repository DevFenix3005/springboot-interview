package com.roberto.interview.configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64;
import com.roberto.interview.management.SecurityMetersService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SecurityJwtConfiguration {

  @Value("${security.jwt.keys.active-public}")
  private String activePublicKey;

  @Value("${security.jwt.keys.active-private}")
  private String activePrivateKey;

  @Value("${security.jwt.keys.previous-public:}")
  private String previousPublicKey;

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    final RSAKey activeKey = buildActiveKey();
    final List<JWK> keys = new ArrayList<>();
    keys.add(activeKey);
    final RSAKey previousKey = buildPreviousPublicKey();
    if (previousKey != null) {
      keys.add(previousKey);
    }
    return new ImmutableJWKSet<>(new JWKSet(keys));
  }

  @Bean
  public JwtDecoder jwtDecoder(final SecurityMetersService securityMetersService, final JWKSource<SecurityContext> jwkSource) {
    final NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSource(jwkSource).build();
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
  public JwtEncoder jwtEncoder(final JWKSource<SecurityContext> jwkSource) {
    return new NimbusJwtEncoder(jwkSource);
  }

  private RSAKey buildActiveKey() {
    final RSAPublicKey publicKey = loadPublicKey(activePublicKey);
    final RSAPrivateKey privateKey = loadPrivateKey(activePrivateKey);
    return new RSAKey.Builder(publicKey)
      .privateKey(privateKey)
      .keyID("active")
      .build();
  }

  private RSAKey buildPreviousPublicKey() {
    if (previousPublicKey == null || previousPublicKey.isBlank()) {
      return null;
    }
    final RSAPublicKey publicKey = loadPublicKey(previousPublicKey);
    return new RSAKey.Builder(publicKey)
      .keyID("previous")
      .build();
  }

  private RSAPublicKey loadPublicKey(final String keyValue) {
    try {
      final byte[] decoded = Base64.from(keyValue).decode();
      final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      final PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
      return (RSAPublicKey) publicKey;
    } catch (Exception ex) {
      throw new IllegalStateException("Invalid RSA public key", ex);
    }
  }

  private RSAPrivateKey loadPrivateKey(final String keyValue) {
    try {
      final byte[] decoded = Base64.from(keyValue).decode();
      final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      final PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
      return (RSAPrivateKey) privateKey;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to load RSA private key. Ensure the key is Base64-encoded in PKCS#8 format.", ex);
    }
  }

}
