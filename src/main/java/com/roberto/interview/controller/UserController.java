package com.roberto.interview.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roberto.interview.models.UserProfile;
import com.roberto.interview.models.UserProfileRequest;
import com.roberto.interview.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserProfile getUserFromContext(@RequestHeader final Map<String, String> header) {
        final String userID = header.get("userId");
        return userService.getUserProfile(UUID.fromString(userID));
    }

    @PostMapping
    public UserProfile addUserToContext(@RequestBody UserProfileRequest payload) {
        final String name = payload.name();
        final List<String> roles = payload.roles();
        return userService.addNewUser(name, roles);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeUserFromContext(@RequestHeader final Map<String, String> header) {
        final String userID = header.get("userId");
        userService.removeUser(UUID.fromString(userID));
        return ResponseEntity.noContent().build();
    }

}
