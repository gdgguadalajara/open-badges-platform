package com.gdgguadalajara.account.model.dto;

public record RegisterAccountRequest(
        String name,
        Boolean acceptedLegal) {
}
