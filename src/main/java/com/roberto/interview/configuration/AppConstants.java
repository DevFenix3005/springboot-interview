package com.roberto.interview.configuration;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class AppConstants {

  public static final String ADMIN = "ROLE_ADMIN";

  public static final String USER = "ROLE_USER";

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

  public static final String AUTHORITIES_CLAIM = "auth";

  public static final String REFRESH_COOKIE_NAME = "refreshToken";

  public static final String SYSTEM_ACCOUNT = "SYSTEM";

  // Access tokens are deliberately short-lived (15 minutes) to reduce impact of token leakage.
  // A longer-lived refresh token (7 days) is used to obtain new access tokens when needed.
  public static final long ACCESS_TOKEN_TTL_SECONDS = 900;

  public static final long REFRESH_TOKEN_TTL_SECONDS = 604800;

  public static final String TOKEN_TYPE_CLAIM = "type";

  public static final String ACCESS_TOKEN_TYPE = "access";

  public static final String REFRESH_TOKEN_TYPE = "refresh";

  private AppConstants() {
    throw new IllegalStateException("Constants class");
  }

}
