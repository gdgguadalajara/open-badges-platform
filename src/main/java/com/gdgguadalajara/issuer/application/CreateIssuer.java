package com.gdgguadalajara.issuer.application;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.issuer.model.dto.CreateIssuerRequest;
import com.gdgguadalajara.issuer.model.dto.IssuerJsonLd;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CreateIssuer {

    private final GetCurrentSession getCurrentSession;
    private final ObjectMapper objectMapper;

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    @Transactional
    public Issuer run(CreateIssuerRequest request) {
        var user = getCurrentSession.run();
        if (!user.isSuperAdmin)
            throw DomainException.forbidden("Solo super administradores pueden crear emosores.");
        var issuer = new Issuer();
        issuer.name = request.name();
        issuer.description = request.description();
        issuer.url = request.url();
        issuer.email = request.email();
        issuer.logoUrl = request.logoUrl();
        issuer.persistAndFlush();
        var issuerJsonLD = IssuerJsonLd.fromEntity(domain, issuer);
        try {
            issuer.jsonPayload = objectMapper.writeValueAsString(issuerJsonLD);
            issuer.persistAndFlush();
        } catch (JsonProcessingException e) {
            throw DomainException.badRequest("Error al generar el JSON-LD del emisores");
        }
        return issuer;
    }

}
