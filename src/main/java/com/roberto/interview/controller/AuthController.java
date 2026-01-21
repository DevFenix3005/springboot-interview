package com.roberto.interview.controller;

import java.security.Principal;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  public AuthController(final AuthenticationManagerBuilder authenticationManagerBuilder,
    final JwtTokenGeneratorService jwtTokenGeneratorService) {
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.jwtTokenGeneratorService = jwtTokenGeneratorService;
  }

  @GetMapping
  public ResponseEntity<Void> isAuthenticated(Principal principal) {
    log.info("REST request to check if the current user is authenticated");
    if (Objects.isNull(principal)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } else {
      log.info(principal.toString());
      return ResponseEntity.noContent().build();
    }
  }

  @PostMapping()
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest loginRequest) {
    final UsernamePasswordAuthenticationToken token =
      new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
    final Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    final LoginResponse loginResponse = jwtTokenGeneratorService.createToken(authentication);
    return ResponseEntity.ok(loginResponse);
  }

}
