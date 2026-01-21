package com.roberto.interview.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.roberto.interview.dtos.user.UserProfileDto;
import com.roberto.interview.service.UserService;

import lombok.AllArgsConstructor;

@Component("userDetailsService")
@AllArgsConstructor(onConstructor_ = { @Autowired })
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserService userService;

  @Override
  @Transactional(readOnly = true)
  public @NullMarked UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return userService.getUserProfileByUsername(username)
      .map(this::createSpringSecurityUser)
      .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
  }

  private UserDetails createSpringSecurityUser(UserProfileDto userProfile) {
    final Set<GrantedAuthority> roles = userProfile.roles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    return User.builder()
      .username(userProfile.username())
      .password(userProfile.password())
      .authorities(roles)
      .build();
  }
}
