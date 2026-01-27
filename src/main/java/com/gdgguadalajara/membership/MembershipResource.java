package com.gdgguadalajara.membership;

import java.util.UUID;

import com.gdgguadalajara.membership.application.CreateIssuerMember;
import com.gdgguadalajara.membership.application.RemoveIssuerMember;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.dto.CreateIssuerMemberRequest;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path("/api/membership")
@RequiredArgsConstructor
public class MembershipResource {

    private final CreateIssuerMember createIssuerMember;
    private final RemoveIssuerMember removeIssuerMember;

    @POST
    @Path("/issuer/{issuerUuid}/account/{accountUuid}")
    @Authenticated
    public IssuerMember create(UUID issuerUuid, UUID accountUuid, CreateIssuerMemberRequest request) {
        return createIssuerMember.run(issuerUuid, accountUuid, request.role());
    }

    @DELETE
    @Path("/issuer/{issuerUuid}/account/{accountUuid}")
    @Authenticated
    public void remove(UUID issuerUuid, UUID accountUuid) {
        removeIssuerMember.run(issuerUuid, accountUuid);
    }
}
