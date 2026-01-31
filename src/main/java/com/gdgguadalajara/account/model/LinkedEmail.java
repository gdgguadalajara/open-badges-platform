package com.gdgguadalajara.account.model;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LinkedEmail extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public String email;

    public Boolean verified = false;

    @JsonIgnore
    public String verificationCode;

    @Column(nullable = false)
    public Instant expiresAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    public Account account;
}
