package com.roberto.interview.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.roberto.interview.dtos.user.UserProfileDto;
import com.roberto.interview.dtos.user.UserProfileRequest;
import com.roberto.interview.dtos.user.UserProfileResponse;
import com.roberto.interview.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userDetailsService;

  public UserController(final UserService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @GetMapping
  public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
    final List<UserProfileDto> users = userDetailsService.getAllUsers();
    final List<UserProfileResponse> response = users.stream()
      .map(user -> new UserProfileResponse(user.id(), user.username()))
      .toList();
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<UserProfileResponse> addUserToContext(@Valid @RequestBody UserProfileRequest payload) {
    final UserProfileDto userProfile = userDetailsService.addNewUser(payload);
    final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(userProfile.id());
    return ResponseEntity.created(uri).body(new UserProfileResponse(userProfile.id(), userProfile.username()));
  }

}
