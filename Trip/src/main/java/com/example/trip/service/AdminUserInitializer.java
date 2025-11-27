
package com.example.trip.service;

import com.example.trip.model.ERole;
import com.example.trip.model.Role;
import com.example.trip.model.Users;
import com.example.trip.service.RoleRepository;
import com.example.trip.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // אם כבר יש admin – יוצאים
        if (userRepository.findByUserName("admin") != null) {
            return;
        }

        // יוצר תפקיד ADMIN אם לא קיים
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(ERole.ROLE_ADMIN);
                    return roleRepository.save(r);
                });

        // יוצר תפקיד USER אם לא קיים
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(ERole.ROLE_USER);
                    return roleRepository.save(r);
                });

        // יוצר את משתמש ה-admin
        Users admin = new Users();
        admin.setUserName("admin");
        admin.setEmail("admin@trip.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Set.of(adminRole, userRole));

        userRepository.save(admin);

        System.out.println("נוצר משתמש מנהל אוטומטית: שם משתמש: admin  |  סיסמה: admin");
    }
}