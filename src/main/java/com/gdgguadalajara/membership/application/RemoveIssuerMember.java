package com.gdgguadalajara.membership.application;

import java.util.UUID;

import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.membership.model.IssuerMember;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class RemoveIssuerMember {

    private final GetCurrentSession getCurrentSession;

    public void run(UUID issuerUuid, UUID accountUuid) {
        var session = getCurrentSession.run();
        if (!session.isSuperAdmin)
            throw DomainException.forbidden("Solo super administradores pueden eliminar miembros de emisores");
        IssuerMember member = IssuerMember.find("issuer.id = ?1 and account.id = ?2", issuerUuid, accountUuid)
                .firstResult();
        if (member == null)
            return;
        run(member);
    }

    @Transactional
    public void run(IssuerMember member) {
        member.delete();
    }
}
