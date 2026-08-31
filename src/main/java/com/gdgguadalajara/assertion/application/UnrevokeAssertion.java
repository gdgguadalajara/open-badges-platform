package com.gdgguadalajara.assertion.application;

import java.util.UUID;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.UnrevokeAssertionResponse;
import com.gdgguadalajara.common.model.DomainException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UnrevokeAssertion {

    private final AssertionMetadata assertionMetadata;

    @Transactional
    public UnrevokeAssertionResponse run(UUID issuerUuid, UUID assertionUuid) {
        Assertion assertion = Assertion.<Assertion>findById(assertionUuid);
        if (assertion == null || !assertion.badgeClass.issuer.id.equals(issuerUuid))
            throw DomainException.notFound("Acreditación no encontrada");

        if (!assertion.isRevoked)
            throw DomainException.badRequest("La credencial no está revocada");

        assertion.isRevoked = false;
        assertion.revocationReason = null;
        assertionMetadata.regenerate(assertion);
        assertion.persist();

        return new UnrevokeAssertionResponse(assertion.id, false);
    }
}