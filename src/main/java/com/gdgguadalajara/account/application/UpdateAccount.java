package com.gdgguadalajara.account.application;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.account.model.dto.UpdateAccountRequest;
import com.gdgguadalajara.assertion.application.CreateAssertion;
import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.authentication.application.GetCurrentSession;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UpdateAccount {

    private final GetCurrentSession getCurrentSession;

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    @Transactional
    public Account run(UpdateAccountRequest request) {
        var account = getCurrentSession.run();
        account.fullName = request.name();
        account.persistAndFlush();
        var assertions = Assertion.<Assertion>list("account = ?1", account);
        for (var assertion : assertions) {
            assertion.htmlPayload = CreateAssertion.Templates.htmlPayload(domain, assertion).render()
                    .replaceAll("(?s)", "")
                    .replaceAll("(?s)\\s+", " ")
                    .replaceAll("> <", "><")
                    .replaceAll("\\s+\\{", "{")
                    .replaceAll("\\{\\s+", "{")
                    .replaceAll(";\\s+", ";")
                    .trim();
            assertion.persist();
        }
        Assertion.flush();
        return account;
    }
}
