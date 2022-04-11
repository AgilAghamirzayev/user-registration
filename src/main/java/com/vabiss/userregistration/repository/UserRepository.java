package com.vabiss.userregistration.repository;

import com.vabiss.userregistration.entity.UserEntity;
import com.vabiss.userregistration.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailAndStatusNot(String username, Status status);

    Optional<UserEntity> findByIdAndStatusNot(Long id, Status status);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u " +
            "SET u.enabled = TRUE WHERE u.email = ?1")
    int enableAppUser(String email);
}
