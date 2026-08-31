package com.gdgguadalajara.assertion.application;

import java.time.Instant;
import java.util.UUID;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.RevokeAssertionRequest;
import com.gdgguadalajara.assertion.model.dto.RevokeAssertionResponse;
import com.gdgguadalajara.common.model.DomainException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class RevokeAssertion {

    private final AssertionMetadata assertionMetadata;

    @Transactional
    public RevokeAssertionResponse run(UUID issuerUuid, UUID assertionUuid, RevokeAssertionRequest request) {
        if (request.reason() == null || request.reason().isBlank())
            throw DomainException.badRequest("El motivo de revocación es obligatorio");

        Assertion assertion = Assertion.<Assertion>findById(assertionUuid);
        if (assertion == null || !assertion.badgeClass.issuer.id.equals(issuerUuid))
            throw DomainException.notFound("Acreditación no encontrada");

        if (assertion.isRevoked)
            throw DomainException.badRequest("La credencial ya se encuentra revocada");

        assertion.isRevoked = true;
        assertion.revocationReason = request.reason();
        assertionMetadata.regenerate(assertion);
        assertion.persist();

        return new RevokeAssertionResponse(assertion.id, true, request.reason(), Instant.now());
    }
}