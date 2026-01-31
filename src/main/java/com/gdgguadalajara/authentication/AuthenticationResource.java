package com.gdgguadalajara.authentication;

import java.net.URI;

import com.gdgguadalajara.account.application.RegisterAccount;
import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.account.model.LinkedEmail;
import com.gdgguadalajara.account.model.dto.RegisterAccountRequest;
import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.authentication.model.dto.MeResponse;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.membership.model.IssuerMember;

import io.quarkus.oidc.OidcSession;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@Path("/api/auth")
@AllArgsConstructor
public class AuthenticationResource {

    private final OidcSession oidcSession;
    private final GetCurrentSession getCurrentSession;
    private final RegisterAccount registerAccount;

    @GET
    @Path("/me")
    @Authenticated
    public MeResponse me() {
        var account = getCurrentSession.run();
        var memberships = IssuerMember.<IssuerMember>list("account", account);
        var emails = LinkedEmail.<LinkedEmail>list("account", account);
        return new MeResponse(account, memberships, emails.stream().map(e -> e.email).toList());
    }

    @POST
    @Path("/register")
    @Authenticated
    public Account register(RegisterAccountRequest request) {
        if (request.acceptedLegal() == null || !request.acceptedLegal())
            throw DomainException.badRequest("Debe aceptar los términos y condiciones");
        return registerAccount.run(request.name());
    }

    @GET
    @Path("/login")
    @Authenticated
    public Response login() {
        try {
            getCurrentSession.run();
            return Response.seeOther(URI.create("/login")).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/register")).build();
        }
    }

    @GET
    @Path("/logout")
    @Authenticated
    public Uni<Response> logout() {
        return oidcSession.logout()
                .onItem().transform(v -> Response.seeOther(URI.create("/")).build());
    }
}
