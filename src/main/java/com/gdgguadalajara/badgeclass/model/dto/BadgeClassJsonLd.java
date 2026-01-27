package com.gdgguadalajara.badgeclass.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdgguadalajara.badgeclass.model.BadgeClass;

public record BadgeClassJsonLd(
        @JsonProperty("@context") String context,
        String type,
        String id,
        String name,
        String description,
        String image,
        Criteria criteria,
        String issuer) {
    public record Criteria(String narrative) {
    }

    public static BadgeClassJsonLd fromEntity(String baseUrl, BadgeClass entity) {
        String canonicalId = String.format("%s/api/v2/badges/%s", baseUrl, entity.id);
        String issuerUrl = String.format("%s/api/v2/issuers/%s", baseUrl, entity.issuer.id);
        String imageUrl = String.format("%s/api/storage/images/%s", baseUrl, entity.image.id);

        return new BadgeClassJsonLd(
                "https://w3id.org/openbadges/v2",
                "BadgeClass",
                canonicalId,
                entity.name,
                entity.description,
                imageUrl,
                new Criteria(entity.criteriaMd),
                issuerUrl);
    }
}
