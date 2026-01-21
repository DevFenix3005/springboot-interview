package com.roberto.interview.dtos.login;

import java.io.Serializable;

public record LoginResponse(
  String token,
  long issuedAt,
  long expiresAt
) implements Serializable {
}
