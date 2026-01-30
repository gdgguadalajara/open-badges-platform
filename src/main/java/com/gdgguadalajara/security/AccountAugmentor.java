package com.gdgguadalajara.security;

import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.assertion.application.ClaimAssertions;

import io.quarkus.oidc.UserInfo;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountAugmentor implements SecurityIdentityAugmentor {

    private final ClaimAssertions claimAssertions;

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.security.super-admins")
    Optional<List<String>> superAdmins;

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous())
            return Uni.createFrom().item(identity);

        UserInfo userInfo = identity.getAttribute("userinfo");
        String email = null;
        String name = userInfo.getString("name");

        if (userInfo != null)
            email = userInfo.getString("email");
        if (email == null)
            email = identity.getPrincipal().getName();
        if (email == null || email.isBlank())
            return Uni.createFrom().item(identity);

        final String finalEmail = email;
        final String finalName = name != null ? name : "";
        return context.runBlocking(() -> performAugmentation(identity, finalEmail, finalName));
    }

    @ActivateRequestContext
    public SecurityIdentity performAugmentation(SecurityIdentity identity, String email, String name) {
        var account = Account.<Account>find("email", email).firstResult();

        if (account == null && isSuperAdmin(email))
            account = createSuperAdmin(email, name);
        if (account != null)
            claimAssertions.run(account);

        return identity;
    }

    private boolean isSuperAdmin(String email) {
        return superAdmins.map(list -> list.contains(email)).orElse(false);
    }

    @Transactional
    Account createSuperAdmin(String email, String name) {
        var account = new Account();
        account.email = email;
        account.fullName = name;
        account.isSuperAdmin = true;
        account.persistAndFlush();
        return account;
    }
}
