package com.example.trip.security;

import com.example.trip.model.Role;
import com.example.trip.model.Users;
import com.example.trip.service.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user=userRepository.findByName(username);
        if (user==null)
            throw new UsernameNotFoundException("user not found");
        List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
        for(Role role:user.getRoles())
        {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }
        return new CustomUserDetails(username,user.getPassword(),grantedAuthorities);
    }
}
