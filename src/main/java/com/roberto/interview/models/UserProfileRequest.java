package com.roberto.interview.models;

import java.util.List;

public record UserProfileRequest(
        String name,
        List<String> roles
) {
}
