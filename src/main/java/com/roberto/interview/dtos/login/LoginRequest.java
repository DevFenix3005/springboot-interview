package com.roberto.interview.dtos.login;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
  @NotNull @NotEmpty String username,
  @NotNull @NotEmpty String password
) implements Serializable {
}
