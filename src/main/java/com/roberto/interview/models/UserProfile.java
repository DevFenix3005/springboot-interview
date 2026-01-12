package com.roberto.interview.models;

import java.util.List;
import java.util.UUID;

public class UserProfile {

    private UUID uuid;

    private String name;

    private List<String> roles;

    private UserProfile(final UUID uuid, final String name, final List<String> roles) {
        this.uuid = uuid;
        this.name = name;
        this.roles = roles;
    }

    public static UserProfile create(final String name, final List<String> roles) {
        return new UserProfile(UUID.randomUUID(), name, roles);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }
}
