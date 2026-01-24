package com.roberto.interview.security;

import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.roberto.interview.configuration.AppConstants;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {
  @Override
  public @NullMarked Optional<String> getCurrentAuditor() {
    return SecurityUtils.getCurrentUserLogin().or(() -> Optional.of(AppConstants.SYSTEM_ACCOUNT));
  }
}
