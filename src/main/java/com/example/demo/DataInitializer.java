package com.example.demo;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public DataInitializer(RoleRepository roleRepository,UserRepository userRepository,PasswordEncoder passwordEncoder)
    {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initRoles()
    {
        String[] roleNames = {"ROLE_ADMIN","ROLE_DOCTOR","ROLE_PATIENT","ROLE_HEALTHCAREMANAGER","ROLE_HOSPITALSTAFF"};
        for(String name:roleNames)
        {
            if(roleRepository.findByName(name)==null)
            {
                Role role = new Role(name);
                roleRepository.save(role);
            }
        }
    }
    @PostConstruct
    public void addAdmin()
    {
        Role existRole = roleRepository.findByName("ROLE_ADMIN");
        if(existRole==null)
        {
            Role newRole = new Role("ROLE_ADMIN");
            existRole = roleRepository.save(newRole);
        }
        User user = userRepository.findByRoles(existRole);
        if(user==null)
        {
            Set<Role> roles = new HashSet<Role>();
            roles.add(existRole);
            String encodePassword = passwordEncoder.encode("Admin12345");
            User newuser = new User("Admin",encodePassword,"rusira42103@gmail.com");
            newuser.setRoles(roles);
            userRepository.save(newuser);
        }
    }
}
