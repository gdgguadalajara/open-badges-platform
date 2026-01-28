package com.gdgguadalajara.account.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Account extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(unique = true)
    public String email;

    @Column(nullable = false)
    public String fullName;

    @Column(nullable = false)
    public Boolean isSuperAdmin = false;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public Instant createdAt;
}
