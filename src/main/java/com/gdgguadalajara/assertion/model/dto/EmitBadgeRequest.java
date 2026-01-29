package com.gdgguadalajara.assertion.model.dto;

import java.util.List;

public record EmitBadgeRequest(
        List<String> emails,
        String evidenceUrl) {
}
