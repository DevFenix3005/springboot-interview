package com.roberto.interview.security;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.roberto.interview.domain.models.UserProfile;
import com.roberto.interview.domain.repository.UserProfileRepository;

@Component("securityUtils")
public class SecurityUtils {

  private final UserProfileRepository userProfileRepository;

  public SecurityUtils(final UserProfileRepository userProfileRepository) {
    this.userProfileRepository = userProfileRepository;
  }

  public Optional<String> getCurrentUserLogin() {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
  }

  public UserProfile getCurrentUserProfileLogin()
    throws UserPrincipalNotFoundException {
    return getCurrentUserLogin()
      .map(userProfileRepository::findByUsername)
      .orElseThrow(() -> new UserPrincipalNotFoundException("User not found"));
  }

  private String extractPrincipal(Authentication authentication) {
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
