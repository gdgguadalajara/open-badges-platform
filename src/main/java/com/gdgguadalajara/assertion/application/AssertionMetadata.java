package com.gdgguadalajara.assertion.application;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.Hibernate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.AssertionJsonLd;
import com.gdgguadalajara.common.model.DomainException;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class AssertionMetadata {

    private final ObjectMapper objectMapper;

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    public void regenerate(Assertion assertion) {
        Hibernate.initialize(assertion.badgeClass);
        Hibernate.initialize(assertion.badgeClass.issuer);
        Hibernate.initialize(assertion.badgeClass.image);
        try {
            var jsonLd = AssertionJsonLd.fromEntity(domain, assertion);
            assertion.jsonPayload = objectMapper.writeValueAsString(jsonLd);
        } catch (JsonProcessingException e) {
            throw DomainException.badRequest("Error al generar el JSON-LD de la Assertion");
        }
        assertion.htmlPayload = CreateAssertion.Templates.htmlPayload(domain, assertion).render()
                .replaceAll("(?s)", "")
                .replaceAll("(?s)\\s+", " ")
                .replaceAll("> <", "><")
                .replaceAll("\\s+\\{", "{")
                .replaceAll("\\{\\s+", "{")
                .replaceAll(";\\s+", ";")
                .trim();
    }
}