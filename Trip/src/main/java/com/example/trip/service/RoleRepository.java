package com.example.trip.service;

import com.example.trip.model.ERole;
import com.example.trip.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

//    user<user> findById(ERole eRole);
}
