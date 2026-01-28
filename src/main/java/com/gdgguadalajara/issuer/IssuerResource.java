package com.gdgguadalajara.issuer;

import java.util.List;
import java.util.UUID;

import com.gdgguadalajara.common.PageBuilder;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.common.model.PaginatedResponse;
import com.gdgguadalajara.common.model.dto.PaginationRequestParams;
import com.gdgguadalajara.issuer.application.CreateIssuer;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.issuer.model.dto.CreateIssuerRequest;
import com.gdgguadalajara.membership.model.IssuerMember;
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
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String jsondl(UUID uuid) {
        var issuer = Issuer.<Issuer>findById(uuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");
        return issuer.jsonPayload;
    }

    @GET
    @Path("/{uuid}/members")
    @Authenticated
    @SuperAdmin
    public PaginatedResponse<IssuerMember> getMembers(UUID uuid, @BeanParam @Valid PaginationRequestParams params) {
        return PageBuilder.of(IssuerMember.<IssuerMember>find("issuer.id = ?1", uuid), params.page, params.size);
    }

    @GET
    @Path("/{uuid}/revocations")
    public List<IssuerMember> revocations(UUID uuid) {
        return List.of();
    }

}
