package com.roberto.interview.service.impl;

import java.time.Instant;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.roberto.interview.configuration.AppConstants;
import com.roberto.interview.dtos.login.LoginResponse;
import com.roberto.interview.service.JwtTokenGeneratorService;

@Service
public class JwtTokenGeneratorServiceImpl implements JwtTokenGeneratorService {

  private final JwtEncoder jwtEncoder;

  public JwtTokenGeneratorServiceImpl(final JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  @Override
  public LoginResponse createAccessToken(final Authentication authentication) {
    final String authorities = authoritiesFromAuthentication(authentication);
    return createAccessToken(authentication.getName(), authorities);
  }

  @Override
  public LoginResponse createAccessToken(final String subject, final String authorities) {
    final JwtClaimsSet jwtClaimsSet = createJwtClaimSet(AppConstants.ACCESS_TOKEN_TTL_SECONDS, subject, authorities, AppConstants.ACCESS_TOKEN_TYPE);
    final JwsHeader jwsHeader = JwsHeader.with(AppConstants.JWT_ALGORITHM).type("JWT").build();
    final String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    return new LoginResponse(token, jwtClaimsSet.getIssuedAt().toEpochMilli(), jwtClaimsSet.getExpiresAt().toEpochMilli());
  }

  @Override
  public String createRefreshToken(final Authentication authentication) {
    final String authorities = authoritiesFromAuthentication(authentication);
    final JwtClaimsSet jwtClaimsSet =
      createJwtClaimSet(AppConstants.REFRESH_TOKEN_TTL_SECONDS, authentication.getName(), authorities, AppConstants.REFRESH_TOKEN_TYPE);
    final JwsHeader jwsHeader = JwsHeader.with(AppConstants.JWT_ALGORITHM).type("JWT").build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
  }

  private static @NonNull JwtClaimsSet createJwtClaimSet(
    final long timeToLiveSeconds,
    final String subject,
    final String authorities,
    final String tokenType) {
    final Instant now = Instant.now();
    final Instant exp = now.plusSeconds(timeToLiveSeconds);
    return JwtClaimsSet.builder().issuer("self")
      .issuedAt(now)
      .expiresAt(exp)
      .subject(subject)
      .claim(AppConstants.AUTHORITIES_CLAIM, authorities)
      .claim(AppConstants.TOKEN_TYPE_CLAIM, tokenType)
      .build();
  }

  private String authoritiesFromAuthentication(final Authentication authentication) {
    return authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));
  }

}
