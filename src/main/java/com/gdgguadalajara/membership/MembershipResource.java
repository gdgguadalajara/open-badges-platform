package com.gdgguadalajara.membership;

import java.util.UUID;

import com.gdgguadalajara.membership.application.CreateIssuerMember;
import com.gdgguadalajara.membership.application.RemoveIssuerMember;
import com.gdgguadalajara.membership.application.UpdateIssuerMember;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.dto.CreateIssuerMemberRequest;
import com.gdgguadalajara.security.annotations.SuperAdmin;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path("/api/memberships")
@RequiredArgsConstructor
public class MembershipResource {

    private final CreateIssuerMember createIssuerMember;
    private final UpdateIssuerMember updateIssuerMember;
    private final RemoveIssuerMember removeIssuerMember;

    @POST
    @Path("/issuers/{issuerUuid}/accounts")
    @Authenticated
    @SuperAdmin
    public IssuerMember create(UUID issuerUuid, CreateIssuerMemberRequest request) {
        return createIssuerMember.run(issuerUuid, request);
    }

    @PUT
    @Path("/issuers/{issuerUuid}/accounts/{accountUuid}")
    @Authenticated
    @SuperAdmin
    public void update(UUID issuerUuid, UUID accountUuid, CreateIssuerMemberRequest request) {
        updateIssuerMember.run(issuerUuid, accountUuid, request.role());
    }

    @DELETE
    @Path("/issuers/{issuerUuid}/accounts/{accountUuid}")
    @Authenticated
    @SuperAdmin
    public void remove(UUID issuerUuid, UUID accountUuid) {
        removeIssuerMember.run(issuerUuid, accountUuid);
    }
}
