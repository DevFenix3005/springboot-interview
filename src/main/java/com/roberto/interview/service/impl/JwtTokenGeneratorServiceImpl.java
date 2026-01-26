package com.roberto.interview.service.impl;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.roberto.interview.dtos.login.LoginResponse;
import com.roberto.interview.security.SecurityUtils;
import com.roberto.interview.service.JwtTokenGeneratorService;

@Service
public class JwtTokenGeneratorServiceImpl implements JwtTokenGeneratorService {

  private static final long ACCESS_TOKEN_TTL_SECONDS = 900;
  private static final long REFRESH_TOKEN_TTL_SECONDS = 604800;
  private static final String TOKEN_TYPE_CLAIM = "type";
  private static final String ACCESS_TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN_TYPE = "refresh";

  private final JwtEncoder jwtEncoder;

  public JwtTokenGeneratorServiceImpl(final JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  @Override
  public LoginResponse createAccessToken(final Authentication authentication) {
    final String authorities = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));
    return createAccessToken(authentication.getName(), authorities);
  }

  @Override
  public LoginResponse createAccessToken(final String subject, final String authorities) {
    final Instant now = Instant.now();
    final Instant exp = now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS);

    final JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
      .issuer("self")
      .issuedAt(now)
      .expiresAt(exp)
      .subject(subject)
      .claim(SecurityUtils.AUTHORITIES_CLAIM, authorities)
      .claim(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);

    final JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).type("JWT").build();

    final String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();

    return new LoginResponse(token, now.toEpochMilli(), exp.toEpochMilli());
  }

  @Override
  public String createRefreshToken(final Authentication authentication) {
    final String authorities = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));
    return createRefreshToken(authentication.getName(), authorities);
  }

  @Override
  public String createRefreshToken(final String subject, final String authorities) {
    final Instant now = Instant.now();
    final Instant exp = now.plusSeconds(REFRESH_TOKEN_TTL_SECONDS);

    final JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
      .issuer("self")
      .issuedAt(now)
      .expiresAt(exp)
      .subject(subject)
      .claim(SecurityUtils.AUTHORITIES_CLAIM, authorities)
      .claim(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);

    final JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).type("JWT").build();

    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
  }

  @Override
  public long getRefreshTokenTtlSeconds() {
    return REFRESH_TOKEN_TTL_SECONDS;
  }

}
