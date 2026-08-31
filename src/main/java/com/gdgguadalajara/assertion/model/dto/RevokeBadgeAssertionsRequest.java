package com.gdgguadalajara.assertion.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RevokeBadgeAssertionsRequest(
        @NotBlank(message = "El motivo de revocación es obligatorio") String reason,
        List<UUID> assertionIds) {
}