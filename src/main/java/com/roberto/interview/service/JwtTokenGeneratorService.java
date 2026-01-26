package com.roberto.interview.service;

import org.springframework.security.core.Authentication;

import com.roberto.interview.dtos.login.LoginResponse;

public interface JwtTokenGeneratorService {

  LoginResponse createAccessToken(final Authentication authentication);

  LoginResponse createAccessToken(final String subject, final String authorities);

  String createRefreshToken(final Authentication authentication);

  String createRefreshToken(final String subject, final String authorities);

  long getRefreshTokenTtlSeconds();

}
