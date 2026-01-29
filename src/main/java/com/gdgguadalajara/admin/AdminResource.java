package com.gdgguadalajara.admin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.badgeclass.model.BadgeClass;
import com.gdgguadalajara.common.PageBuilder;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.common.model.PaginatedResponse;
import com.gdgguadalajara.common.model.dto.PaginationRequestParams;
import com.gdgguadalajara.issuer.model.Issuer;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.storage.application.BakeImage;

import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

@Path("/api/admin")
@AllArgsConstructor
public class AdminResource {

    private final GetCurrentSession getCurrentSession;
    private final BakeImage bakeImage;

    @GET
    @Path("/issuers/{uuid}")
    public Issuer readIssuerByUuid(UUID uuid) {
        var issuer = Issuer.<Issuer>findById(uuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");
        return issuer;
    }

    @GET
    @Path("/badges/{uuid}")
    public BadgeClass readBadgeByUuid(UUID uuid) {
        var baadge = BadgeClass.<BadgeClass>findById(uuid);
        if (baadge == null)
            throw DomainException.notFound("Credencial no encontrada");
        return baadge;
    }

    @GET
    @Path("/me/assertions")
    @Authenticated
    public PaginatedResponse<Assertion> readAssertions(@BeanParam @Valid PaginationRequestParams request) {
        var account = getCurrentSession.run();
        return PageBuilder.of(Assertion.find("account", account), request);
    }

    @GET
    @Path("/assertions/{assertionUuid}")
    @Authenticated
    public Assertion readAssertionByUuid(UUID assertionUuid) {
        var account = getCurrentSession.run();
        var assertion = Assertion.<Assertion>findById(assertionUuid);
        if (assertion == null)
            throw DomainException.notFound("Acreditación no encontrada");
        if (account.isSuperAdmin
                || assertion.account.id.equals(account.id)
                || IssuerMember.find(
                        "account.id = ?1 and issuer.id = ?2",
                        account.id,
                        assertion.badgeClass.issuer.id).count() > 0)
            return assertion;
        throw DomainException.notFound("Acreditación no encontrada");
    }

    @GET
    @Path("/assertions/{assertionUuid}/bakedimage")
    @Authenticated
    public Response readAssertionBakedImageByUuid(UUID assertionUuid) {
        var account = getCurrentSession.run();
        var assertion = Assertion.<Assertion>findById(assertionUuid);
        if (assertion == null)
            throw DomainException.notFound("Acreditación no encontrada");
        try {
            var imageBaked = bakeImage.bakePng(assertion.badgeClass.image.data, assertion);
            if (account.isSuperAdmin
                    || assertion.account.id.equals(account.id)
                    || IssuerMember.find(
                            "account.id = ?1 and issuer.id = ?2",
                            account.id,
                            assertion.badgeClass.issuer.id).count() > 0) {
                var fileName = assertion.badgeClass.name.toLowerCase()
                        .replaceAll("[^a-z0-9]", "-")
                        .replaceAll("-+", "-")
                        + ".png";
                var encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                        .replace("+", "%20");
                return Response.ok(imageBaked)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" + encodedFileName)
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400, immutable")
                        .build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        throw DomainException.notFound("Acreditación no encontrada");
    }
}
