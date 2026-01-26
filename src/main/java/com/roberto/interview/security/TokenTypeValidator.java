package com.roberto.interview.security;

import java.util.Objects;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import com.roberto.interview.configuration.AppConstants;

public class TokenTypeValidator implements OAuth2TokenValidator<Jwt> {
  private final String expectedTokenType;

  public TokenTypeValidator(final String expectedTokenType) {
    this.expectedTokenType = expectedTokenType;
  }

  @Override
  public OAuth2TokenValidatorResult validate(final Jwt token) {
    final String tokenType = token.getClaimAsString(AppConstants.TOKEN_TYPE_CLAIM);
    if (Objects.equals(expectedTokenType, tokenType)) {
      return OAuth2TokenValidatorResult.success();
    }
    final String errorDescription =
        String.format("Invalid token type. Expected: %s, but received: %s", expectedTokenType, tokenType);
    final OAuth2Error error = new OAuth2Error("invalid_token_type", errorDescription, null);
    return OAuth2TokenValidatorResult.failure(error);
  }
}
