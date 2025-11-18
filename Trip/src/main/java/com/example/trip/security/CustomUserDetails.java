package com.example.trip.security;

import com.example.trip.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {
    public CustomUserDetails(String name, String password, Collection<? extends GrantedAuthority> authorities) {
        super(name, password, authorities);
    }
}
