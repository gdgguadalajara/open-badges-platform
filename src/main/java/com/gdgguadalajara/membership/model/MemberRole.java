package com.gdgguadalajara.membership.model;

public enum MemberRole {
    OWNER, // Puede gestionar otros admins
    ADMIN, // Puede crear BadgeClasses y emitir Assertions
}
