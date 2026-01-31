package com.gdgguadalajara.assertion.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.Hibernate;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.account.model.LinkedEmail;
import com.gdgguadalajara.assertion.model.Assertion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.regex.Pattern;

@ApplicationScoped
public class ClaimAssertions {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    @Transactional
    public void run(Account account) {
        Set<String> validEmails = new HashSet<>();
        validEmails.add(account.email);

        var verifiedLinks = LinkedEmail.<LinkedEmail>find("account = ?1 and verified = true",
                account).list();

        for (var linked : verifiedLinks)
            validEmails.add(linked.email);

        List<String> emailHashes = validEmails.stream()
                .map(email -> email != null ? email.trim().toLowerCase() : "")
                .filter(email -> !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches())
                .map(DigestUtils::sha256Hex)
                .collect(Collectors.toList());

        List<Assertion> orphans = Assertion.find("recipientEmail IN ?1 AND account IS NULL",
                emailHashes).list();

        if (orphans.isEmpty())
            return;

        for (Assertion assertion : orphans) {
            var duplicate = Assertion.count("badgeClass = ?1 and account = ?2",
                    assertion.badgeClass, account) > 0;
            if (duplicate) {
                assertion.delete();
                continue;
            }
            assertion.account = account;
            assertion.isPublic = true;
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
            assertion.persist();
        }
        Account.flush();
    }
}
