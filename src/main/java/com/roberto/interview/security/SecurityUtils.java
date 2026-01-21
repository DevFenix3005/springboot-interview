package com.roberto.interview.security;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;

import com.roberto.interview.domain.models.UserProfile;
import com.roberto.interview.domain.repository.UserProfileRepository;

public class SecurityUtils {

  public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

  public static final String AUTHORITIES_CLAIM = "auth";

  public static final String USER_ID_CLAIM = "userId";

  private SecurityUtils() {
  }

  public static Optional<String> getCurrentUserLogin() {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
  }

  public static UserProfile getCurrentUserProfileLogin(final UserProfileRepository userProfileRepository)
    throws UserPrincipalNotFoundException {
    return SecurityUtils.getCurrentUserLogin()
      .map(userProfileRepository::findByUsername)
      .orElseThrow(() -> new UserPrincipalNotFoundException("User not found"));
  }

  private static String extractPrincipal(Authentication authentication) {
    if (authentication == null) {
      return null;
    } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
      return springSecurityUser.getUsername();
    } else if (authentication.getPrincipal() instanceof Jwt jwt) {
      return jwt.getSubject();
    } else if (authentication.getPrincipal() instanceof String s) {
      return s;
    }
    return null;
  }

}
