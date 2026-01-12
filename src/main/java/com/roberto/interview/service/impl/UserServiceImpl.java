package com.roberto.interview.service.impl;

import java.util.List;
import java.util.UUID;

import com.roberto.interview.Context;
import com.roberto.interview.models.UserProfile;
import com.roberto.interview.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public UserProfile getUserProfile(final UUID userId) {
        return Context.getInstance().getUserProfile(userId);
    }

    @Override
    public UserProfile addNewUser(final String name, final List<String> roles) {
        return Context.getInstance().addUserProfile(name, roles);
    }

    @Override
    public void removeUser(final UUID userId) {
        Context.getInstance().removeUserFromContext(userId);
    }
}
