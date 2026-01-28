package com.gdgguadalajara.membership.model.dto;

import java.util.UUID;

import com.gdgguadalajara.membership.model.MemberRole;

public record CreateIssuerMemberRequest(
                UUID accountUuid,
                String email,
                MemberRole role) {

}
