package com.gdgguadalajara.assertion.model.dto;

import jakarta.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RevokeAssertionRequest(
        @NotBlank(message = "El motivo de revocación es obligatorio") String reason) {
}