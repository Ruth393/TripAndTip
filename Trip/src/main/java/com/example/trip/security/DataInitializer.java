package com.example.trip.security;

import com.example.trip.model.ERole;
import com.example.trip.model.Role;
import com.example.trip.model.Users;
import com.example.trip.service.RoleRepository;
import com.example.trip.service.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. איתחול תפקיד USER אם לא קיים
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(ERole.ROLE_USER);
                    return roleRepository.save(role);
                });

        // 2. איתחול תפקיד ADMIN אם לא קיים
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(ERole.ROLE_ADMIN);
                    return roleRepository.save(role);
                });

        // 3. יצירת מנהל מערכת ראשוני אוטומטי (כדי שתוכלי להתחבר איתו)
        String adminEmail = "admin@trip.com";
        if (userRepository.findByEmail(adminEmail) == null) {
            Users admin = new Users();
            admin.setUserName("SystemAdmin");
            admin.setEmail(adminEmail);
            // סיסמה זמנית למנהל: admin1234
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setImagePath(null);

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println(">> Admin user created automatically: admin@trip.com / admin1234");
        }
    }
}