package com.gdgguadalajara.security.filters;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.UUID;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.membership.model.IssuerMember;
import com.gdgguadalajara.membership.model.MemberRole;
import com.gdgguadalajara.security.annotations.SecuredAction;

@SecuredAction
@Provider
@Priority(Priorities.AUTHORIZATION)
public class SecurityInterceptorFilter implements ContainerRequestFilter {

    @Inject SecurityIdentity identity;
    @Context ResourceInfo resourceInfo;
    @Context UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String email = identity.getPrincipal().getName();
        Account user = Account.find("email", email).firstResult();
        if (user == null) {
            abort(requestContext, "Usuario no registrado");
            return;
        }
        if (user.isSuperAdmin) return;
        String issuerUuidStr = uriInfo.getPathParameters().getFirst("issuerUuid");
        if (issuerUuidStr == null) {
            abort(requestContext, "ID de organización faltante en la ruta");
            return;
        }
        UUID issuerUuid = UUID.fromString(issuerUuidStr);
        IssuerMember membership = IssuerMember.find("account.id = ?1 and issuer.id = ?2", 
                                                    user.id, issuerUuid).firstResult();
        if (membership == null || !hasRequiredRole(membership.role)) {
            abort(requestContext, "No tienes permisos para realizar esta acción en esta comunidad");
        }
    }

    private boolean hasRequiredRole(MemberRole userRole) {
        SecuredAction annotation = resourceInfo.getResourceMethod().getAnnotation(SecuredAction.class);
        if (annotation == null) {
            annotation = resourceInfo.getResourceClass().getAnnotation(SecuredAction.class);
        }
        return annotation != null && Arrays.asList(annotation.value()).contains(userRole);
    }

    private void abort(ContainerRequestContext context, String msg) {
        context.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\": \"" + msg + "\"}").build());
    }
}
