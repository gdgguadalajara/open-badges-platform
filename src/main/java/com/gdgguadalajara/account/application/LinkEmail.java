package com.gdgguadalajara.account.application;

import java.time.Duration;
import java.time.Instant;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.account.model.LinkedEmail;
import com.gdgguadalajara.account.model.dto.LinkEmailRequest;
import com.gdgguadalajara.authentication.application.GetCurrentSession;
import com.gdgguadalajara.common.model.DomainException;
import com.gdgguadalajara.common.utils.TokenUtils;
import com.gdgguadalajara.mail.application.SendLinkEmailMailNotification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@ApplicationScoped
@AllArgsConstructor
public class LinkEmail {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final GetCurrentSession getCurrentSession;
    private final SendLinkEmailMailNotification sendLinkEmailMailNotification;

    @Transactional
    public LinkedEmail run(LinkEmailRequest request) {
        var account = getCurrentSession.run();
        String targetEmail = request.email().toLowerCase().trim();

        if (targetEmail.isEmpty() || !EMAIL_PATTERN.matcher(targetEmail).matches())
            throw DomainException.badRequest("El correo electrónico no es válido.");
        if (account.email.equalsIgnoreCase(targetEmail))
            throw DomainException.badRequest("Este es tu correo principal de inicio de sesión.");
        if (Account.count("email = ?1", targetEmail) > 0)
            throw DomainException.badRequest("Este correo ya está registrado.");
        if (LinkedEmail.count("email = ?1 and verified = true", targetEmail) > 0)
            throw DomainException.badRequest("Este correo ya está registrado.");

        var code = codeGenerator(12);
        var linked = new LinkedEmail();
        linked.email = request.email();
        linked.verificationCode = code;
        linked.expiresAt = Instant.now().plus(Duration.ofMinutes(15));
        linked.account = account;
        linked.persistAndFlush();

        sendLinkEmailMailNotification.run(targetEmail, TokenUtils.encodeToken(targetEmail, code)).replaceWithNull();

        return linked;
    }

    private String codeGenerator(Integer length) {
        return RANDOM.ints(length, 0, CHAR_POOL.length())
                .mapToObj(CHAR_POOL::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}