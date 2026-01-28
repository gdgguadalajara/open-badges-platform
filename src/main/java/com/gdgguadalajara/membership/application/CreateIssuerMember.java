package com.gdgguadalajara.membership.application;

import java.util.UUID;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.MemberRole;
import com.gdgguadalajara.membership.model.dto.CreateIssuerMemberRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateIssuerMember {

    public IssuerMember run(UUID issuerUuid, CreateIssuerMemberRequest request) {
        if (request.accountUuid() == null && request.email() == null)
            throw DomainException.badRequest("Debe especificar una cuenta o un correo electrónico");
        var issuer = Issuer.<Issuer>findById(issuerUuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");
        Account account = null;
        if (request.accountUuid() != null)
            Account.<Account>findById(request.accountUuid());
        if (request.email() != null)
            account = Account.find("email", request.email()).firstResult();
        if (account == null)
            throw DomainException.notFound("Cuenta no encontrada");
        return run(issuer, account, request.role());
    }

    @Transactional
    public IssuerMember run(Issuer issuer, Account account, MemberRole role) {
        var issuerMember = new IssuerMember();
        issuerMember.issuer = issuer;
        issuerMember.account = account;
        issuerMember.role = role;
        issuerMember.persistAndFlush();
        return issuerMember;
    }
}
