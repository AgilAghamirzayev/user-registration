package com.vabiss.userregistration.service.email;

import com.vabiss.userregistration.entity.EmailConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(EmailConfirmationToken token);

    Optional<EmailConfirmationToken> getToken(String token);

    int setConfirmedAt(String token);
}
