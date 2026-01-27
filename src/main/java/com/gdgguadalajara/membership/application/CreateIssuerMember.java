package com.gdgguadalajara.membership.application;

import java.util.UUID;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.MemberRole;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateIssuerMember {

    private final GetCurrentSession getCurrentSession;

    public IssuerMember run(UUID issuerUuid, UUID accountUuid, MemberRole role) {
        var session = getCurrentSession.run();
        if (!session.isSuperAdmin)
            throw DomainException.forbidden("Solo super administradores pueden crear miembros de emisores");
        var issuer = Issuer.<Issuer>findById(issuerUuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");
        var account = Account.<Account>findById(accountUuid);
        if (account == null)
            throw DomainException.notFound("Cuenta no encontrada");
        return run(issuer, account, role);
    }

    @Transactional
    public IssuerMember run(Issuer issuer, Account account, MemberRole role) {
        var issuerMember = new IssuerMember();
        issuerMember.issuer = issuer;
        issuerMember.account = account;
        issuerMember.persistAndFlush();
        return issuerMember;
    }
}
