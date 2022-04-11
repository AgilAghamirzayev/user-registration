package com.vabiss.userregistration.service.email.impl;

import com.vabiss.userregistration.entity.EmailConfirmationToken;
import com.vabiss.userregistration.repository.ConfirmationTokenRepository;
import com.vabiss.userregistration.service.email.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;

    @Override
    public void saveConfirmationToken(EmailConfirmationToken token) {
        repository.save(token);
    }

    @Override
    public Optional<EmailConfirmationToken> getToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return repository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
