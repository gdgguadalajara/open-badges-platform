package com.gdgguadalajara.assertion.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.RevokeBadgeAssertionsRequest;
import com.gdgguadalajara.assertion.model.dto.RevokeBadgeAssertionsResponse;
import com.gdgguadalajara.badgeclass.model.BadgeClass;
import com.gdgguadalajara.common.model.DomainException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class RevokeBadgeAssertions {

    private final AssertionMetadata assertionMetadata;

    @Transactional
    public RevokeBadgeAssertionsResponse run(UUID issuerUuid, UUID badgeClassUuid,
            RevokeBadgeAssertionsRequest request) {
        if (request.reason() == null || request.reason().isBlank())
            throw DomainException.badRequest("El motivo de revocación es obligatorio");

        BadgeClass badge = BadgeClass.<BadgeClass>findById(badgeClassUuid);
        if (badge == null || !badge.issuer.id.equals(issuerUuid))
            throw DomainException.notFound("Credencial no encontrada");

        List<Assertion> targets = resolveTargets(badgeClassUuid, request.assertionIds());

        if (targets.isEmpty())
            throw DomainException.badRequest("No hay credenciales elegibles para revocar");

        List<UUID> revokedIds = new ArrayList<>();
        for (Assertion assertion : targets) {
            assertion.isRevoked = true;
            assertion.revocationReason = request.reason();
            assertionMetadata.regenerate(assertion);
            assertion.persist();
            revokedIds.add(assertion.id);
        }

        return new RevokeBadgeAssertionsResponse(
                issuerUuid,
                badgeClassUuid,
                request.reason(),
                targets.size(),
                revokedIds.size(),
                targets.size() - revokedIds.size(),
                Instant.now(),
                revokedIds);
    }

    private List<Assertion> resolveTargets(UUID badgeClassUuid, List<UUID> assertionIds) {
        if (assertionIds == null || assertionIds.isEmpty())
            return Assertion.<Assertion>find("badgeClass.id = ?1 and isRevoked = false", badgeClassUuid).list();

        var candidates = Assertion.<Assertion>find("id in ?1", assertionIds).list();
        return candidates.stream()
                .filter(a -> a.badgeClass.id.equals(badgeClassUuid) && !a.isRevoked)
                .toList();
    }
}