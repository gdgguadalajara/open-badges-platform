package com.gdgguadalajara.authentication.application;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.common.model.DomainException;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import lombok.RequiredArgsConstructor;

@RequestScoped
@RequiredArgsConstructor
public class GetCurrentSession {

    private final SecurityIdentity identity;
    private Account cachedAccount;

    public Account run() {
        if (cachedAccount != null)
            return cachedAccount;
        var email = identity.getPrincipal().getName();
        var account = Account.<Account>find("email", email).firstResult();
        if (account == null)
            throw DomainException.notFound("Cuenta no encontrada");
        cachedAccount = account;
        return account;
    }
}
