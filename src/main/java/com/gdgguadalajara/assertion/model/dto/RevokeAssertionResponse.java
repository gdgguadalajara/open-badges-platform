package com.gdgguadalajara.assertion.model.dto;

import java.time.Instant;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RevokeAssertionResponse(
        UUID assertionId,
        boolean isRevoked,
        String revocationReason,
        Instant revokedAt) {
}