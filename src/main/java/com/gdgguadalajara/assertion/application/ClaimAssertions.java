package com.gdgguadalajara.assertion.application;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.Hibernate;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.assertion.model.Assertion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ClaimAssertions {

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    @Transactional
    public void run(Account account) {
        var emailsha256 = new DigestUtils(MessageDigestAlgorithms.SHA_256).digestAsHex(account.email);
        List<Assertion> orphans = Assertion.find("recipientEmail = ?1 and account is null",
                emailsha256).list();
        if (orphans.isEmpty())
            return;
        for (Assertion assertion : orphans) {
            assertion.account = account;
            Hibernate.initialize(assertion.badgeClass);
            Hibernate.initialize(assertion.badgeClass.issuer);
            Hibernate.initialize(assertion.badgeClass.image);
            assertion.htmlPayload = CreateAssertion.Templates.htmlPayload(domain, assertion).render()
                    .replaceAll("(?s)", "")
                    .replaceAll("(?s)\\s+", " ")
                    .replaceAll("> <", "><")
                    .replaceAll("\\s+\\{", "{")
                    .replaceAll("\\{\\s+", "{")
                    .replaceAll(";\\s+", ";")
                    .trim();
            assertion.persistAndFlush();
            assertion.persistAndFlush();
        }
    }
}
