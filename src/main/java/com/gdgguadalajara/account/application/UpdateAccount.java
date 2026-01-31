package com.gdgguadalajara.account.application;

import com.gdgguadalajara.account.model.Account;
import com.gdgguadalajara.account.model.dto.UpdateAccountRequest;
import com.gdgguadalajara.authentication.application.GetCurrentSession;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class UpdateAccount {

    private final GetCurrentSession getCurrentSession;

    @Transactional
    public Account run(UpdateAccountRequest request) {
        var account = getCurrentSession.run();
        account.fullName = request.name();
        account.persistAndFlush();
        return account;
    }
}
