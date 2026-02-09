package com.gdgguadalajara.assertion;

import java.util.UUID;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.common.model.DomainException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@Path("/api/v2/assertions")
@AllArgsConstructor
public class AssertionResource {

    @GET
    @Path("/{uuid}")
    @Produces({ MediaType.TEXT_HTML, "application/ld+json", MediaType.APPLICATION_JSON })
    public Response assertion(UUID uuid, @HeaderParam("Accept") String acceptHeader) {
        var assertion = Assertion.<Assertion>findById(uuid);

        if (assertion == null || !assertion.isPublic || assertion.account == null)
            throw DomainException.notFound("Acreditación no encontrada");

        if (acceptHeader != null &&
                (acceptHeader.contains(MediaType.APPLICATION_JSON)
                        || acceptHeader.contains("application/ld+json"))) {

            return Response.ok(assertion.jsonPayload)
                    .type("application/ld+json")
                    .build();
        }

        return Response.ok(assertion.htmlPayload)
                .type(MediaType.TEXT_HTML)
                .build();
    }
}
