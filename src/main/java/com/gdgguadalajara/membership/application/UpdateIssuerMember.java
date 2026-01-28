package com.gdgguadalajara.membership.application;

import java.util.UUID;

import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.MemberRole;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UpdateIssuerMember {

    public IssuerMember run(UUID issuerUuid, UUID accountUuid, MemberRole role) {
        var member = IssuerMember.<IssuerMember>find("issuer.id = ?1 and account.id = ?2", issuerUuid, accountUuid)
                .firstResult();
        if (member == null)
            throw DomainException.notFound("Miembro no encontrado");
        return run(member, role);
    }

    @Transactional
    public IssuerMember run(IssuerMember member, MemberRole role) {
        member.role = role;
        member.persistAndFlush();
        return member;
    }
}
