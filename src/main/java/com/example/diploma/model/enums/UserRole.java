package com.example.diploma.model.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class UserRole implements GrantedAuthority {
//    USER(Set.of(Permission.USER)),
//    MODERATOR(Set.of(Permission.USER, Permission.MODERATE));
//
//    private final Set<Permission> permissions;
//
//    UserRole(Set<Permission> permissions) {
//        this.permissions = permissions;
//    }
//
//    public Set<Permission> getPermissions() {
//        return permissions;
//    }
//
//    public Set<SimpleGrantedAuthority> getAuthorities(){
//        return permissions.stream().map(p -> new SimpleGrantedAuthority(p.getPermission()))
//                .collect(Collectors.toSet());
//    }

    private final String name;

    public UserRole(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
