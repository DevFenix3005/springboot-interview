package com.roberto.interview.dtos.user;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserProfileRequest(
  @NotEmpty @NotNull String username,
  @NotEmpty @NotNull String password,
  @Size(min = 1, max = 99) List<String> roles
) implements Serializable {
}
