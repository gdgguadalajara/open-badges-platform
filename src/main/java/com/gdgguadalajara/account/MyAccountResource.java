package com.gdgguadalajara.account;

import com.gdgguadalajara.account.application.ConfirmLinkedEmail;
import com.gdgguadalajara.account.application.LinkEmail;
import com.gdgguadalajara.account.application.UpdateAccount;
import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.account.model.LinkedEmail;
import com.gdgguadalajara.account.model.dto.LinkEmailRequest;
import com.gdgguadalajara.account.model.dto.UpdateAccountRequest;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@Path("/api/me/account")
@AllArgsConstructor
@Authenticated
public class MyAccountResource {

    private final UpdateAccount updateAccount;
    private final LinkEmail linkEmail;
    private final ConfirmLinkedEmail confirmLinkedEmail;

    @POST
    @Path("/linked-emails")
    public LinkedEmail linkEmail(LinkEmailRequest request) {
        return linkEmail.run(request);
    }

    @GET
    @Path("/confirm-linked-emails/{code}")
    public Response confirmManualEmail(String code) {
        confirmLinkedEmail.run(code);
        return Response.ok().build();
    }

    @PUT
    public Account update(UpdateAccountRequest request) {
        return updateAccount.run(request);
    }
}
