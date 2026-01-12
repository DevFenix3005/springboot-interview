package com.roberto.interview.controller;

import java.util.ArrayList;
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

import com.roberto.interview.Context;
import com.roberto.interview.models.UserProfile;
import com.roberto.interview.models.UserProfileRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public UserProfile getUserFromContext(@RequestHeader final Map<String, String> header) {
        final String userID = header.get("userId");
        return Context.getInstance().getUserProfile(UUID.fromString(userID));
    }

    @PostMapping
    public UserProfile addUserToContext(@RequestBody UserProfileRequest payload) {
        final String name = payload.name();
        final List<String> roles = payload.roles();
        return Context.getInstance().addUserProfile(name, roles);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeUserFromContext(@RequestHeader final Map<String, String> header) {
        final String userID = header.get("username");
        Context.getInstance().removeUserFromContext(UUID.fromString(userID));
        return ResponseEntity.noContent().build();
    }

}
