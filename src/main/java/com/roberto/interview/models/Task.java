package com.roberto.interview.models;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Task(
        UUID id,
        @NotBlank String string,
        @NotNull Priority priority
) {
}
