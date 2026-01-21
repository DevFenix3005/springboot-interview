package com.roberto.interview.dtos.user;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserProfileDto(
  long id,
  String username,
  @JsonIgnore String password,
  @JsonIgnore Set<String> roles
) implements Serializable {
}
