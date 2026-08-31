package com.gdgguadalajara.issuer.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.gdgguadalajara.assertion.model.Assertion;
import com.gdgguadalajara.assertion.model.dto.BadgeIssuanceSummary;
import com.gdgguadalajara.badgeclass.model.BadgeClass;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.common.model.PaginatedResponse;
import com.gdgguadalajara.common.model.PaginationMeta;
import com.gdgguadalajara.common.model.dto.PaginationRequestParams;
import com.gdgguadalajara.issuer.model.Issuer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BuildBadgeIssuanceSummary {

    @Transactional
    public PaginatedResponse<BadgeIssuanceSummary> run(UUID issuerUuid, PaginationRequestParams params) {
        Issuer issuer = Issuer.<Issuer>findById(issuerUuid);
        if (issuer == null)
            throw DomainException.notFound("Emisor no encontrado");

        var badgeQuery = BadgeClass.find("issuer.id", issuerUuid);
        var badgePage = badgeQuery.page(
                (params.page > 0 ? params.page - 1 : 0), params.size);
        List<BadgeClass> badges = badgePage.list();
        long totalRecords = badgePage.count();
        int totalPages = (totalRecords == 0) ? 0 : (int) Math.ceil((double) totalRecords / params.size);
        Integer nextPage = (params.page < totalPages) ? params.page + 1 : null;
        Integer prevPage = (params.page > 1) ? params.page - 1 : null;

        List<BadgeIssuanceSummary> summaries = new ArrayList<>();
        for (BadgeClass badge : badges)
            summaries.add(buildSummary(badge));

        var meta = new PaginationMeta(totalRecords, params.page, totalPages, nextPage, prevPage);
        return new PaginatedResponse<>(summaries, meta);
    }

    @Transactional
    public BadgeIssuanceSummary run(UUID issuerUuid, UUID badgeClassUuid) {
        BadgeClass badge = BadgeClass.<BadgeClass>findById(badgeClassUuid);
        if (badge == null || !badge.issuer.id.equals(issuerUuid))
            throw DomainException.notFound("Credencial no encontrada");
        return buildSummary(badge);
    }

    private BadgeIssuanceSummary buildSummary(BadgeClass badge) {
        long issued = Assertion.count("badgeClass.id = ?1", badge.id);
        long claimed = Assertion.count("badgeClass.id = ?1 and account is not null", badge.id);
        long revoked = Assertion.count("badgeClass.id = ?1 and isRevoked = true", badge.id);
        long pending = issued - claimed;
        double claimRate = issued == 0 ? 0.0 : Math.round((claimed * 10000.0) / issued) / 100.0;
        String imageUrl = badge.image != null ? "/api/storage/images/" + badge.image.id : null;

        return new BadgeIssuanceSummary(
                badge.id, badge.name, imageUrl, issued, claimed, pending, revoked, claimRate);
    }
}