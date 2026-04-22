package com.example.trip.service;

import com.example.trip.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Users findByUserName(String name);
    Users findByEmail(String email);
}