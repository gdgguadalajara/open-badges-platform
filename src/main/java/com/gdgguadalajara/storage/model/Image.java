package com.gdgguadalajara.storage.model;

import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Image extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @JdbcTypeCode(Types.BINARY)
    @Column(nullable = false)
    @JsonIgnore
    public byte[] data;

    @Column(name = "content_type", nullable = false)
    public String contentType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public Instant createdAt;
}
