package com.gdgguadalajara.assertion.model.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdgguadalajara.assertion.model.Assertion;

public record AssertionJsonLd(
        @JsonProperty("@context") String context,
        String id,
        String type,
        Recipient recipient,
        String badge,
        String issuedOn,
        Verification verification,
        List<Evidence> evidence) {

    public record Recipient(String type, String identity, boolean hashed) {
    }

    public record Verification(String type) {
    }

    public record Evidence(
            @JsonProperty("@context") String context,
            String description,
            String name,
            String genre,
            String type) {
    }

    public static AssertionJsonLd fromEntity(String baseUrl, Assertion entity) {
        var canonicalId = String.format("%s/api/v2/assertions/%s", baseUrl, entity.id);
        var badgeUrl = String.format("%s/api/v2/badges/%s", baseUrl, entity.badgeClass.id);
        List<Evidence> evidences = new ArrayList<>();
        if (entity.evidence != null && !entity.evidence.isBlank())
            evidences.add(new Evidence(
                    "https://w3id.org/openbadges/v2",
                    entity.evidence,
                    "Evidence",
                    "IdEvidence",
                    "Evidence"));
        return new AssertionJsonLd(
                "https://w3id.org/openbadges/v2",
                canonicalId,
                "Assertion",
                new Recipient("email", "SHA256$" + entity.recipientEmail, true),
                badgeUrl,
                entity.issuedOn.toString(),
                new Verification("hosted"),
                evidences);
    }
}
