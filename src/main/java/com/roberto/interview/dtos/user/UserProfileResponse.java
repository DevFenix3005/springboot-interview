package com.roberto.interview.dtos.user;

import java.io.Serializable;

public record UserProfileResponse(
  long id,
  String username
) implements Serializable {
}
