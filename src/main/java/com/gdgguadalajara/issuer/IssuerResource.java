package com.gdgguadalajara.issuer;

import java.util.UUID;

import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.issuer.application.CreateIssuer;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.issuer.model.dto.CreateIssuerRequest;

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
            throw DomainException.notFound("Issuer no encontrado");
        return issuer.jsonPayload;
    }

}
