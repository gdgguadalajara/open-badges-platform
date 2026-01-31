package com.gdgguadalajara.assertion.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.AssertionJsonLd;
import com.gdgguadalajara.assertion.model.dto.EmitBadgeRequest;
import com.gdgguadalajara.badgeclass.model.BadgeClass;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.mail.application.SendBadgeMailNotification;
import com.gdgguadalajara.mail.model.dto.SendBadgeMailNotificationRequest;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
@Transactional
public class CreateAssertion {

    private final ObjectMapper objectMapper;
    private final SendBadgeMailNotification sendBadgeMailNotification;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance htmlPayload(String baseUrl, Assertion assertion);
    }

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    public List<Assertion> run(UUID badgeClassUuid, EmitBadgeRequest request) {
        List<SendBadgeMailNotificationRequest> emailRequests = new ArrayList<>();
        BadgeClass badgeClass = BadgeClass.findById(badgeClassUuid);
        if (badgeClass == null)
            throw DomainException.notFound("Badge no encontrada");
        for (String email : request.emails()) {
            var emailsha256 = DigestUtils.sha256Hex(email);
            var exists = Assertion.count("badgeClass = ?1 and recipientEmail = ?2",
                    badgeClass, emailsha256) > 0;
            if (exists)
                continue;
            var assertion = run(badgeClass, email, emailsha256, request.evidenceUrl());
            emailRequests.add(new SendBadgeMailNotificationRequest(assertion, email));
        }
        sendBadgeMailNotification.run(emailRequests);
        return emailRequests.stream().map(r -> r.assertion()).toList();
    }

    public Assertion run(BadgeClass badge, String email, String emailsha256, String evidence) {
        var assertion = new Assertion();
        assertion.badgeClass = badge;
        assertion.recipientEmail = emailsha256;
        assertion.evidence = evidence;
        assertion.isPublic = false;
        assertion.persistAndFlush();

        var jsonLd = AssertionJsonLd.fromEntity(domain, assertion);
        try {
            assertion.jsonPayload = objectMapper.writeValueAsString(jsonLd);
            assertion.persistAndFlush();
        } catch (JsonProcessingException e) {
            throw DomainException.badRequest("Error al generar el JSON-LD de la Assertion");
        }

        var account = Account.<Account>find("email", email).firstResult();
        Hibernate.initialize(assertion.badgeClass);
        Hibernate.initialize(assertion.badgeClass.issuer);
        Hibernate.initialize(assertion.badgeClass.image);
        if (account != null) {
            assertion.account = account;
            assertion.isPublic = true;
            assertion.htmlPayload = Templates.htmlPayload(domain, assertion).render()
                    .replaceAll("(?s)", "")
                    .replaceAll("(?s)\\s+", " ")
                    .replaceAll("> <", "><")
                    .replaceAll("\\s+\\{", "{")
                    .replaceAll("\\{\\s+", "{")
                    .replaceAll(";\\s+", ";")
                    .trim();
            assertion.persistAndFlush();
        }
        return assertion;
    }
}