package com.gdgguadalajara.assertion.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record RevokedAssertion(
        String id,
        String reason) {

    public RevokedAssertion {
        if (reason == null)
            reason = "";
    }

    public static RevokedAssertion fromEntity(String baseUrl, com.gdgguadalajara.assertion.model.Assertion assertion) {
        var canonicalId = String.format("%s/api/v2/assertions/%s", baseUrl, assertion.id);
        return new RevokedAssertion(canonicalId, assertion.revocationReason);
    }
}