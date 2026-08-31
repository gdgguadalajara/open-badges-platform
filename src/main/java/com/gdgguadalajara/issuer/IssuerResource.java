package com.gdgguadalajara.issuer;

import java.util.UUID;

import com.gdgguadalajara.common.PageBuilder;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.common.model.PaginatedResponse;
import com.gdgguadalajara.common.model.dto.PaginationRequestParams;
import com.gdgguadalajara.issuer.application.BuildRevocationList;
import com.gdgguadalajara.issuer.application.CreateIssuer;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.issuer.model.dto.CreateIssuerRequest;
import com.gdgguadalajara.issuer.model.dto.RevocationListJsonLd;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.MemberRole;
import com.gdgguadalajara.security.annotations.SecuredAction;
import com.gdgguadalajara.security.annotations.SuperAdmin;

import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/api/v2/issuers")
@RequiredArgsConstructor
public class IssuerResource {

    private final CreateIssuer createIssuer;
    private final BuildRevocationList buildRevocationList;

    @POST
    @Authenticated
    @SuperAdmin
    public Issuer create(CreateIssuerRequest request) {
        return createIssuer.run(request);
    }

    @GET
    public PaginatedResponse<Issuer> read(@BeanParam @Valid PaginationRequestParams params) {
        return PageBuilder.of(Issuer.findAll(), params.page, params.size);
    }

    @GET
    @Path("/{issuerUuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String jsondl(UUID issuerUuid) {
        var issuer = Issuer.<Issuer>findById(issuerUuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");
        return issuer.jsonPayload;
    }

    @GET
    @Path("/{issuerUuid}/members")
    @Authenticated
    @SecuredAction({ MemberRole.OWNER })
    public PaginatedResponse<IssuerMember> getMembers(UUID issuerUuid, @BeanParam @Valid PaginationRequestParams params) {
        return PageBuilder.of(IssuerMember.<IssuerMember>find("issuer.id = ?1", issuerUuid), params.page, params.size);
    }

    @GET
    @Path("/{issuerUuid}/revocations")
    @Produces(MediaType.APPLICATION_JSON)
    public RevocationListJsonLd revocations(UUID issuerUuid) {
        return buildRevocationList.run(issuerUuid);
    }

}
