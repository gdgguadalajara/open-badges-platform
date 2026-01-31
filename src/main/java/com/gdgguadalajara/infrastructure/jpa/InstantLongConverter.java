package com.gdgguadalajara.infrastructure.jpa;

import java.time.Instant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InstantLongConverter implements AttributeConverter<Instant, Long> {

    @Override
    public Long convertToDatabaseColumn(Instant instant) {
        return (instant == null) ? null : instant.toEpochMilli();
    }

    @Override
    public Instant convertToEntityAttribute(Long dbData) {
        return (dbData == null) ? null : Instant.ofEpochMilli(dbData);
    }
}
