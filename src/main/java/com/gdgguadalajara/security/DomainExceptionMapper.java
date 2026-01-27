package com.gdgguadalajara.security;

import com.gdgguadalajara.common.model.DomainException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Override
    public Response toResponse(DomainException exception) {
        return Response.status(exception.getStatusCode())
                .entity("{\"message\": \"" + exception.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
