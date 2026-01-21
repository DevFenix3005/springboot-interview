package com.roberto.interview.dtos.task;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TaskResponse(
  Long id,
  String title,
  String priority,
  boolean completed,
  LocalDateTime createdAt
) implements Serializable {
}
