package com.gdgguadalajara.account.application;

import java.time.Instant;

import com.gdgguadalajara.account.model.LinkedEmail;
import com.gdgguadalajara.assertion.application.ClaimAssertions;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.common.utils.TokenUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class ConfirmLinkedEmail {

    private final ClaimAssertions claimAssertions;

    @Transactional
    public void run(String code) {
        var payload = TokenUtils.decodeToken(code);
        var linked = LinkedEmail.<LinkedEmail>find("email = ?1 and verificationCode = ?2",
                payload[0], payload[1]).firstResult();
        if (linked == null || linked.expiresAt.isBefore(Instant.now()))
            throw DomainException.badRequest("Fallo la verificación del correo electrónico.");
        linked.verified = true;
        linked.verificationCode = null;
        linked.persistAndFlush();
        claimAssertions.run(linked.account);
    }

}
