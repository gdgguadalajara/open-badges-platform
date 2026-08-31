package com.gdgguadalajara.issuer.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.RevokedAssertion;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.issuer.model.dto.RevocationListJsonLd;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BuildRevocationList {

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    public RevocationListJsonLd run(UUID issuerUuid) {
        Issuer issuer = Issuer.<Issuer>findById(issuerUuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");

        List<RevokedAssertion> revokedAssertions = Assertion
                .<Assertion>find("badgeClass.issuer.id = ?1 and isRevoked = true", issuerUuid)
                .stream()
                .map(a -> RevokedAssertion.fromEntity(domain, a))
                .collect(Collectors.toList());

        var issuerId = String.format("%s/api/v2/issuers/%s", domain, issuer.id);
        var revocationListId = issuerId + "/revocations";

        return new RevocationListJsonLd(
                "https://w3id.org/openbadges/v2",
                revocationListId,
                "RevocationList",
                issuerId,
                revokedAssertions);
    }
}