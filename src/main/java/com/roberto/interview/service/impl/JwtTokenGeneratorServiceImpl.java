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

  private final JwtEncoder jwtEncoder;

  public JwtTokenGeneratorServiceImpl(final JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  @Override
  public LoginResponse createToken(final Authentication authentication) {
    final String authorities = authentication.getAuthorities().stream()
      .map(GrantedAuthority::getAuthority)
      .collect(Collectors.joining(" "));
    final Instant now = Instant.now();
    final Instant exp = now.plusSeconds(86400); // 24 hours

    final JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
      .issuer("self")
      .issuedAt(now)
      .expiresAt(exp)
      .subject(authentication.getName())
      .claim(SecurityUtils.AUTHORITIES_CLAIM, authorities);

    final JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).type("JWT").build();

    final String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();

    return new LoginResponse(token, now.toEpochMilli(), exp.toEpochMilli());
  }

}
