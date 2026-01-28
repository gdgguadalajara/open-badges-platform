package com.gdgguadalajara.security.filters;

import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.security.annotations.SuperAdmin;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;

@SuperAdmin
@Provider
@Priority(Priorities.AUTHORIZATION)
@RequiredArgsConstructor
public class SuperAdminFilter implements ContainerRequestFilter {

    private final GetCurrentSession getCurrentSession;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var account = getCurrentSession.run();
        if (account == null || !account.isSuperAdmin)
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("{\"message\":\"Acceso denegado: Se requieren permisos de administrador de plataforma.\"}")
                            .build());
    }
}
