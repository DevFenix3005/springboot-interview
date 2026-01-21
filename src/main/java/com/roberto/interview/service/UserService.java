package com.roberto.interview.service;

import java.util.List;
import java.util.Optional;

import com.roberto.interview.dtos.user.UserProfileRequest;
import com.roberto.interview.dtos.user.UserProfileDto;

public interface UserService {

  List<UserProfileDto> getAllUsers();

  Optional<UserProfileDto> getUserProfileByUsername(final String username);

  UserProfileDto addNewUser(final UserProfileRequest userProfileRequest);

  void removeUser(final Long userId);

}
