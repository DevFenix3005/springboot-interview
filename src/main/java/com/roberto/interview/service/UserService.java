package com.roberto.interview.service;

import java.util.List;
import java.util.UUID;

import com.roberto.interview.models.UserProfile;

public interface UserService {

    UserProfile getUserProfile(final UUID userId);

    UserProfile addNewUser(final String name, final List<String> roles);

    void removeUser(final UUID userId);

}
