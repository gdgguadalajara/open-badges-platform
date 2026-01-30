package com.gdgguadalajara.account.application;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.assertion.application.ClaimAssertions;
import com.gdgguadalajara.common.model.DomainException;

import io.quarkus.oidc.UserInfo;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class RegisterAccount {

    private final SecurityIdentity identity;
    private final ClaimAssertions claimAssertions;

    @Transactional
    public Account run(String name) {
        UserInfo userInfo = identity.getAttribute("userinfo");
        String email = userInfo.getString("email");
        if (email == null)
            throw DomainException.badRequest("Email no encontrado");
        Account account = new Account();
        account.email = email;
        account.fullName = name;
        account.persistAndFlush();
        claimAssertions.run(account);
        return account;
    }
}
