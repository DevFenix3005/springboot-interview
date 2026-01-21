package com.roberto.interview.dtos.task;

import java.io.Serializable;

import com.roberto.interview.domain.models.Priority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequest(
        @NotBlank String title,
        @NotNull Priority priority
) implements Serializable {
}
