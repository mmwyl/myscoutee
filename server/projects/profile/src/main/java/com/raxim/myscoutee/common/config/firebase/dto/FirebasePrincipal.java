package com.raxim.myscoutee.common.config.firebase.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.raxim.myscoutee.profile.data.document.mongo.Role;
import com.raxim.myscoutee.profile.data.document.mongo.User;

public class FirebasePrincipal implements UserDetails {
    private final User user;
    private final List<Role> roles;

    public FirebasePrincipal(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }

    public User getUser() {
        return user;
    }
    
    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
