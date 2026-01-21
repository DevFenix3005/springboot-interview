package com.roberto.interview.service;

import org.springframework.security.core.Authentication;

import com.roberto.interview.dtos.login.LoginResponse;

public interface JwtTokenGeneratorService {

  LoginResponse createToken(final Authentication authentication);

}
