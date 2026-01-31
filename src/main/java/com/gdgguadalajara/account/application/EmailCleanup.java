package com.gdgguadalajara.account.application;

import java.time.Instant;

import com.gdgguadalajara.account.model.LinkedEmail;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmailCleanup {

    @Transactional
    @Scheduled(every = "1h")
    public void deleteExpiredCodes() {
        var deleted = LinkedEmail.delete("verified = false AND expiresAt < ?1", Instant.now());
        if (deleted > 0)
            System.out.println("Sincronización: Se eliminaron " + deleted + " códigos expirados.");
    }
}
