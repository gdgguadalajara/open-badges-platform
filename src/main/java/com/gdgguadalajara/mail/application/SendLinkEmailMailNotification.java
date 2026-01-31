package com.gdgguadalajara.mail.application;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.gdgguadalajara.mail.ResendClient;
import com.gdgguadalajara.mail.model.dto.ResendRequest;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class SendLinkEmailMailNotification {

    @Inject
    @RestClient
    ResendClient resendClient;

    @ConfigProperty(name = "resend.from")
    String FROM;

    @ConfigProperty(name = "resend.api.key")
    String apiKey;

    @ConfigProperty(name = "com.gdgguadalajara.open-badges-platform.domain")
    public String domain;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance email(String codeLink, String code);
    }

    public Uni<Void> run(String email, String code) {
        var request = new ResendRequest(
                FROM,
                email,
                "Verifica tu correo para reclamar tus insignias",
                Templates.email(domain + "/profile?verify=" + code, code).render());

        try {
            resendClient.sendEmail("Bearer " + apiKey, List.of(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uni.createFrom().voidItem();
    }
}
