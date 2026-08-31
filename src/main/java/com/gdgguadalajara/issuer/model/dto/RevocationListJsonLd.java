package com.gdgguadalajara.issuer.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RevocationListJsonLd(
        @JsonProperty("@context") String context,
        String id,
        String type,
        String issuer,
        List<com.gdgguadalajara.assertion.model.dto.RevokedAssertion> revokedAssertions) {

    public RevocationListJsonLd {
        if (context == null)
            context = "https://w3id.org/openbadges/v2";
        if (type == null)
            type = "RevocationList";
        if (revokedAssertions == null)
            revokedAssertions = List.of();
    }
}