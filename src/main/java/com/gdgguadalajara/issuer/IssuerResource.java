package com.gdgguadalajara.issuer;

import java.util.List;
import java.util.UUID;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.issuer.application.CreateIssuer;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.issuer.model.dto.CreateIssuerRequest;
import com.gdgguadalajara.membership.model.IssuerMember;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;

@Path("/api/v2/issuer")
@RequiredArgsConstructor
public class IssuerResource {

    private final CreateIssuer createIssuer;

    @POST
    @Authenticated
    public Issuer create(CreateIssuerRequest request) {
        return createIssuer.run(request);
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
    public List<IssuerMember> getMembers(UUID uuid) {
        return IssuerMember.<IssuerMember>find("issuer.id = ?1", uuid).list();
    }

}
