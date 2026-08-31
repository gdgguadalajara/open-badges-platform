package com.gdgguadalajara.assertion.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RevokeBadgeAssertionsResponse(
        UUID issuerId,
        UUID badgeClassId,
        String reason,
        int total,
        int revoked,
        int skipped,
        Instant revokedAt,
        List<UUID> assertionIds) {
}