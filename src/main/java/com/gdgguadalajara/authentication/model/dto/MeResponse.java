package com.gdgguadalajara.authentication.model.dto;

import java.util.List;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.membership.model.IssuerMember;

public record MeResponse(
                Account account,
                List<IssuerMember> memberships,
                List<String> linkedEmails) {

}
