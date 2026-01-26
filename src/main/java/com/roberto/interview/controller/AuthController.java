package com.roberto.interview.controller;

import java.security.Principal;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roberto.interview.dtos.login.LoginRequest;
import com.roberto.interview.dtos.login.LoginResponse;
import com.roberto.interview.security.SecurityUtils;
import com.roberto.interview.service.JwtTokenGeneratorService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final String REFRESH_COOKIE_NAME = "refreshToken";
  private static final String REFRESH_TOKEN_TYPE = "refresh";

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  private final JwtTokenGeneratorService jwtTokenGeneratorService;

  private final JwtDecoder jwtDecoder;

  public AuthController(final AuthenticationManagerBuilder authenticationManagerBuilder,
    final JwtTokenGeneratorService jwtTokenGeneratorService,
    final JwtDecoder jwtDecoder) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.jwtTokenGeneratorService = jwtTokenGeneratorService;
    this.jwtDecoder = jwtDecoder;
  }

  @GetMapping
  public ResponseEntity<Void> isAuthenticated(Principal principal) {
    log.info("REST request to check if the current user is authenticated");
    if (Objects.isNull(principal)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @PostMapping()
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest loginRequest) {
    final UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
    final Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final LoginResponse loginResponse = jwtTokenGeneratorService.createAccessToken(authentication);
    final String refreshToken = jwtTokenGeneratorService.createRefreshToken(authentication);
    final ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
      .httpOnly(true)
      .secure(true)
      .sameSite("Strict")
      .path("/api/auth/refresh")
      .maxAge(jwtTokenGeneratorService.getRefreshTokenTtlSeconds())
      .build();
    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
      .body(loginResponse);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final Jwt decodedToken;
    try {
      decodedToken = jwtDecoder.decode(refreshToken);
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    final String tokenType = decodedToken.getClaimAsString("type");
    if (!REFRESH_TOKEN_TYPE.equals(tokenType)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final String authorities = decodedToken.getClaimAsString(SecurityUtils.AUTHORITIES_CLAIM);
    final LoginResponse loginResponse = jwtTokenGeneratorService.createAccessToken(decodedToken.getSubject(), authorities);
    final String rotatedRefreshToken = jwtTokenGeneratorService.createRefreshToken(decodedToken.getSubject(), authorities);
    final ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, rotatedRefreshToken)
      .httpOnly(true)
      .secure(true)
      .sameSite("Strict")
      .path("/api/auth/refresh")
      .maxAge(jwtTokenGeneratorService.getRefreshTokenTtlSeconds())
      .build();
    return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
      .body(loginResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    final ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
      .httpOnly(true)
      .secure(true)
      .sameSite("Strict")
      .path("/api/auth/refresh")
      .maxAge(0)
      .build();
    return ResponseEntity.noContent()
      .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
      .build();
  }

}
