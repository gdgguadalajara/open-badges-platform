package com.gdgguadalajara.issuer;

import java.util.List;
import java.util.UUID;

import com.gdgguadalajara.assertion.application.CreateAssertion;
import com.gdgguadalajara.assertion.application.RevokeAssertion;
import com.gdgguadalajara.assertion.application.UnrevokeAssertion;
import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.AssertionFilterParams;
import com.gdgguadalajara.assertion.model.dto.BadgeAssertionItem;
import com.gdgguadalajara.assertion.model.dto.EmitBadgeRequest;
import com.gdgguadalajara.assertion.model.dto.RevokeAssertionRequest;
import com.gdgguadalajara.assertion.model.dto.RevokeAssertionResponse;
import com.gdgguadalajara.assertion.model.dto.UnrevokeAssertionResponse;
import com.gdgguadalajara.common.model.PaginatedResponse;
import com.gdgguadalajara.common.model.dto.PaginationRequestParams;
import com.gdgguadalajara.issuer.application.ListBadgeAssertions;
import com.gdgguadalajara.membership.model.MemberRole;
import com.gdgguadalajara.security.annotations.OrgRole;

import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;

@Path("/api/v2/issuers/{issuerUuid}/badges/{badgeClassUuid}/assertions")
@RequiredArgsConstructor
public class IssuerAssertionResource {

    private final CreateAssertion createAssertion;
    private final ListBadgeAssertions listBadgeAssertions;
    private final RevokeAssertion revokeAssertion;
    private final UnrevokeAssertion unrevokeAssertion;

    @POST
    @Authenticated
    @OrgRole({ MemberRole.OWNER })
    public List<Assertion> create(UUID issuerUuid, UUID badgeClassUuid, EmitBadgeRequest request) {
        return createAssertion.run(badgeClassUuid, request);
    }

    @GET
    @Authenticated
    @OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })
    public PaginatedResponse<BadgeAssertionItem> read(UUID issuerUuid, UUID badgeClassUuid,
            @BeanParam @Valid AssertionFilterParams filters,
            @BeanParam @Valid PaginationRequestParams params) {
        return listBadgeAssertions.run(issuerUuid, badgeClassUuid, filters, params);
    }

    @PATCH
    @Path("/{assertionUuid}/revoke")
    @Authenticated
    @OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })
    public RevokeAssertionResponse revoke(UUID issuerUuid, UUID badgeClassUuid, UUID assertionUuid,
            @Valid RevokeAssertionRequest request) {
        return revokeAssertion.run(issuerUuid, assertionUuid, request);
    }

    @PATCH
    @Path("/{assertionUuid}/unrevoke")
    @Authenticated
    @OrgRole({ MemberRole.OWNER, MemberRole.ADMIN })
    public UnrevokeAssertionResponse unrevoke(UUID issuerUuid, UUID badgeClassUuid, UUID assertionUuid) {
        return unrevokeAssertion.run(issuerUuid, assertionUuid);
    }

}