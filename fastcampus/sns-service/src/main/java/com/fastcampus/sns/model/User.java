package com.fastcampus.sns.model;

import com.fastcampus.sns.model.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonIgnoreProperties
public class User implements UserDetails {

    private Integer id;
    private String username;
    private String password;

    private UserRole userRole;
    private Timestamp registerAt;
    private Timestamp updateAt;
    private Timestamp deleteAt;


    public static User fromEntity(UserEntity entity) {
        log.debug("entity : {}",entity.toString());
        return new User(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getUserRole(),
                entity.getRegisterAt(),
                entity.getUpdateAt(),
                entity.getDeletedAt()
        );
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
