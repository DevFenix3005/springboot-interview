package com.roberto.interview.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.roberto.interview.domain.models.Role;
import com.roberto.interview.domain.models.UserProfile;
import com.roberto.interview.domain.repository.RoleRepository;
import com.roberto.interview.domain.repository.UserProfileRepository;
import com.roberto.interview.dtos.user.UserProfileDto;
import com.roberto.interview.dtos.user.UserProfileRequest;
import com.roberto.interview.service.UserService;

@Service
public class UserServiceImpl implements UserService {

  private final UserProfileRepository userProfileRepository;

  private final RoleRepository roleRepository;

  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(final UserProfileRepository userProfileRepository, final RoleRepository roleRepository,
    final PasswordEncoder passwordEncoder) {
    this.userProfileRepository = userProfileRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<UserProfileDto> getAllUsers() {
    return userProfileRepository.findAll().stream().map(this::mapToUserProfileResponse).toList();
  }

  @Override
  public Optional<UserProfileDto> getUserProfileByUsername(final String username) {
    return userProfileRepository.findByUsernameAndRolesIsContaining(username).map(this::mapToUserProfileResponse);
  }

  @Override
  public UserProfileDto addNewUser(final UserProfileRequest userProfileRequest) {
    final UserProfile userProfile = UserProfile.builder()
      .username(userProfileRequest.username())
      .password(passwordEncoder.encode(userProfileRequest.password()))
      .roles(processRoles(userProfileRequest.roles()))
      .build();
    final UserProfile newUserProfile = userProfileRepository.save(userProfile);
    return mapToUserProfileResponse(newUserProfile);
  }

  private Set<Role> processRoles(final List<String> roles) {
    if (CollectionUtils.isEmpty(roles)) {
      return Collections.emptySet();
    }
    return roles
      .stream()
      .map(role -> roleRepository.findByRoleName(role).orElseGet(() -> createNewRole(role)))
      .collect(Collectors.toSet());
  }

  private Role createNewRole(final String roleName) {
    final Role newRole = Role.builder().roleName(roleName).build();
    return roleRepository.save(newRole);
  }

  private UserProfileDto mapToUserProfileResponse(final UserProfile userProfile) {
    return new UserProfileDto(
      userProfile.getId(),
      userProfile.getUsername(),
      userProfile.getPassword(),
      userProfile.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet())
    );
  }

  @Override
  public void removeUser(final Long userId) {
    if (userProfileRepository.existsById(userId)) {
      userProfileRepository.deleteById(userId);
    } else {
      throw new UsernameNotFoundException("User not found");
    }
  }

}
