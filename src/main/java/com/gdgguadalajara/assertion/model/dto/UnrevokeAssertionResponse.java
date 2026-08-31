package com.gdgguadalajara.assertion.model.dto;

import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UnrevokeAssertionResponse(
        UUID assertionId,
        boolean isRevoked) {
}