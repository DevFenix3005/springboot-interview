package com.roberto.interview.controller;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roberto.interview.configuration.AppConstants;
import com.roberto.interview.dtos.login.LoginRequest;
import com.roberto.interview.dtos.login.LoginResponse;
import com.roberto.interview.service.JwtTokenGeneratorService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final JwtTokenGeneratorService jwtTokenGeneratorService;

  private final JwtDecoder jwtDecoder;

  public AuthController(final AuthenticationManagerBuilder authenticationManagerBuilder,
    final JwtTokenGeneratorService jwtTokenGeneratorService, final JwtDecoder jwtDecoder) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.jwtTokenGeneratorService = jwtTokenGeneratorService;
    this.jwtDecoder = jwtDecoder;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest loginRequest) {
    final UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
    final Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
    final String refreshToken = jwtTokenGeneratorService.createRefreshToken(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final LoginResponse loginResponse = jwtTokenGeneratorService.createAccessToken(authentication);
    final String responseCookie = createRefreshCookie(refreshToken, AppConstants.REFRESH_TOKEN_TTL_SECONDS);
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie).body(loginResponse);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(
    @CookieValue(name = AppConstants.REFRESH_COOKIE_NAME, required = false) final String refreshToken) {
    if (Objects.isNull(refreshToken) || refreshToken.isBlank()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    final Jwt decodedToken;

    try {
      decodedToken = jwtDecoder.decode(refreshToken);
    } catch (final Exception e) {
      log.error("Error decoding refresh token", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final String tokenType = decodedToken.getClaimAsString(AppConstants.TOKEN_TYPE_CLAIM);
    if (!AppConstants.REFRESH_TOKEN_TYPE.equals(tokenType)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final String authorities = decodedToken.getClaimAsString(AppConstants.AUTHORITIES_CLAIM);
    final LoginResponse loginResponse = jwtTokenGeneratorService.createAccessToken(decodedToken.getSubject(), authorities);
    final String rotatedRefreshToken = jwtTokenGeneratorService.createRefreshToken(decodedToken.getSubject(), authorities);
    final String responseCookie = createRefreshCookie(rotatedRefreshToken, AppConstants.REFRESH_TOKEN_TTL_SECONDS);
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie).body(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    final String refreshCookie = createRefreshCookie("", 0);
    return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie).build();
  }

  private String createRefreshCookie(final String refreshTokenValue, final long ttlSeconds) {
    return ResponseCookie.from(AppConstants.REFRESH_COOKIE_NAME, refreshTokenValue)
      .httpOnly(true)
      .secure(true)
      .sameSite("Strict")
      .path("/api/auth/refresh")
      .maxAge(ttlSeconds)
      .build()
      .toString();
  }

}
