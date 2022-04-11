package com.vabiss.userregistration.service.impl;

import com.vabiss.userregistration.dto.UserDTO;
import com.vabiss.userregistration.entity.EmailConfirmationToken;
import com.vabiss.userregistration.entity.UserEntity;
import com.vabiss.userregistration.entity.enums.Status;
import com.vabiss.userregistration.exception.*;
import com.vabiss.userregistration.mapper.UserMapper;
import com.vabiss.userregistration.repository.UserRepository;
import com.vabiss.userregistration.service.UserService;
import com.vabiss.userregistration.service.email.ConfirmationTokenService;
import com.vabiss.userregistration.service.email.EmailSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final UserMapper MAPPER = UserMapper.INSTANCE;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public void registration(UserDTO request) {

        userRepository.findByEmailAndStatusNot(request.getEmail(), Status.DELETED)
                .ifPresent(user -> { throw new EmailAlreadyExistException(request.getEmail()); });

        UserEntity userEntity = MAPPER.userDtoToUser(request);
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        userEntity.setStatus(Status.OFFLINE);
        UserEntity savedUserEntity = userRepository.save(userEntity);

        String token = emailConfirmation(userEntity);

        log.info("IN register - user: {} successfully returned token: ", token);

        String link = "http://localhost:8080/api/account/confirm?token=" + token;
        emailSender.send(request.getEmail(), link, String.format("Hi %s, please enter to link and activate your account", savedUserEntity.getName()));

        UserDTO userDTO = MAPPER.userToUserDTO(savedUserEntity);
        log.info("IN register - user: {} successfully registered", userDTO);
    }

    @Transactional
    @Override
    public String confirmToken(String token) {
        EmailConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new TokenNotFoundException(token));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new EmailAlreadyConfirmedException(confirmationToken.getUserEntity().getEmail());
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EmailTokenExpiredException(token);
        }

        confirmationTokenService.setConfirmedAt(token);
        userRepository.enableAppUser(confirmationToken.getUserEntity().getEmail());

        return "Confirmed";
    }

    private String emailConfirmation(UserEntity entity) {
        String token = UUID.randomUUID().toString();
        EmailConfirmationToken confirmationToken = new EmailConfirmationToken();
        confirmationToken.setToken(token);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        confirmationToken.setUserEntity(entity);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        log.info("IN register - user: {} successfully email confirmed", confirmationToken);
        return token;
    }

    @Override
    public UserDTO getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmailAndStatusNot(email, Status.DELETED).orElseThrow(() -> {
            throw new UsernameNotFoundException(email);
        });

        UserDTO userDTO = MAPPER.userToUserDTO(userEntity);
        log.info("IN getUserDetailsByUsername - user: {} successfully found", userDTO);

        return userDTO;
    }

    @Override
    public void delete() {
        Long id = getIdFromToken();

        UserEntity user = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException("User", id); });
        user.setStatus(Status.DELETED);

        log.info("IN getUserDetailsByUsername - user: {} successfully deleted", user.getName());
    }

    @Override
    public UserDTO login() {
        Long id = getIdFromToken();
        UserEntity userEntity = userRepository.findByIdAndStatusNot(id, Status.DELETED).orElseThrow(() -> {
            throw new UserNotFoundException(id); });

        if (!userEntity.getEnabled()) {
            throw new UserNotEnabledException(userEntity.getName());
        }

        if (userEntity.getStatus().equals(Status.ONLINE)) {
            throw new UserAlreadyWasLoginException(userEntity.getName());
        }

        userEntity.setStatus(Status.ONLINE);
        userRepository.save(userEntity);

        UserDTO userDTO = MAPPER.userToUserDTO(userEntity);
        log.info("IN getUserDetailsByUsername - user: {} successfully login", userDTO);

        return userDTO;
    }

    private Long getIdFromToken() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmailAndStatusNot(email, Status.DELETED).orElseThrow(() -> {
            throw new UsernameNotFoundException(email); });

        return new User(userEntity.getEmail(), userEntity.getPassword(), true, true, true, true, new ArrayList<>());
    }
}
