package com.gdgguadalajara.assertion.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.badgeclass.model.BadgeClass;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Assertion extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_class_id", nullable = false)
    public BadgeClass badgeClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    public Account account;

    @Column(nullable = false)
    public String recipientEmail;

    @Column(columnDefinition = "TEXT")
    @JsonRawValue
    public String jsonPayload;

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    public String htmlPayload;

    @Column(nullable = false)
    public Boolean isPublic = true;

    @Column(nullable = false)
    public Boolean isRevoked = false;

    public String revocationReason;
    
    public String evidence = "";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public Instant issuedOn;
}
