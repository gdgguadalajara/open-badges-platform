package com.gdgguadalajara.authentication;

import java.net.URI;

import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.authentication.model.dto.MeResponse;
import com.gdgguadalajara.membership.model.IssuerMember;

import io.quarkus.oidc.OidcSession;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@Path("/api/auth")
@AllArgsConstructor
public class AuthenticationResource {

    private final OidcSession oidcSession;
    private final GetCurrentSession getCurrentSession;

    @GET
    @Path("/me")
    @Authenticated
    public MeResponse me() {
        var account = getCurrentSession.run();
        var memberships = IssuerMember.<IssuerMember>list("account", account);
        return new MeResponse(account, memberships);
    }

    @GET
    @Path("/login")
    @Authenticated
    public Response login() {
        return Response.seeOther(URI.create("/login")).build();
    }

    @GET
    @Path("/logout")
    @Authenticated
    public Uni<Response> logout() {
        return oidcSession.logout()
                .onItem().transform(v -> Response.seeOther(URI.create("/")).build());
    }
}
