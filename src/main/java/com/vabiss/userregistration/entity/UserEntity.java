package com.vabiss.userregistration.entity;

import com.vabiss.userregistration.entity.enums.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "users")
@RequiredArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String password;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserEntity UserEntity = (UserEntity) o;

        return Objects.equals(getId(), UserEntity.getId());
    }

    @Override
    public int hashCode() {
        return 562048007;
    }
}
