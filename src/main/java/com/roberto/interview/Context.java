package com.roberto.interview;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.roberto.interview.models.UserProfile;

public class Context {

    private static Context instance;

    private final Map<UUID, UserProfile> userProfile = new ConcurrentHashMap<>();

    private Context() {
    }

    public static Context getInstance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    public UserProfile getUserProfile(final UUID userId) {
        return userProfile.get(userId);
    }

    public UserProfile addUserProfile(final String name, List<String> roles) {
        final UserProfile profile = UserProfile.create(name, roles);
        this.userProfile.put(profile.getUuid(), profile);
        return profile;
    }

    public void removeUserFromContext(final UUID userId) {
        userProfile.remove(userId);
    }

}
