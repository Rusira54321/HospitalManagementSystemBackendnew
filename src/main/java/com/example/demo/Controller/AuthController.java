package com.example.demo.Controller;


import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalStaffRepository hospitalStaffRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    public AuthController(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, HospitalRepository hospitalRepository, HospitalStaffRepository hospitalStaffRepository,DoctorRepository doctorRepository,PatientRepository patientRepository)
    {
        this.hospitalStaffRepository = hospitalStaffRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostMapping("/register/hospitalStaff")
    public ResponseEntity<?> register(@RequestBody HospitalStaff hospitalStaff,@RequestParam String hospitalID,@RequestParam String role)
    {
        try {
            Optional<User> existingUser = userRepository.findByUsername(hospitalStaff.getUsername());
            if(existingUser.isPresent())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already registered");
            }
            Optional<User> existEmail = userRepository.findByEmail(hospitalStaff.getEmail());
            if(existEmail.isPresent())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already exist");
            }
            Role existRole = roleRepository.findByName("ROLE_"+role.toUpperCase());
            if (existRole == null) {
                Role newrole = new Role("ROLE_"+role.toUpperCase());
                existRole = roleRepository.save(newrole);
            }
            Set<Role> roles = new HashSet<Role>();
            roles.add(existRole);
            String encodePassword = passwordEncoder.encode(hospitalStaff.getPassword());
            hospitalStaff.setPassword(encodePassword);
            hospitalStaff.setRoles(roles);
            Long id = Long.parseLong(hospitalID);
            Hospital existHospital = hospitalRepository.findByHospitalId(id);
            if (existHospital == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("The hospital is not found");
            }
            hospitalStaff.setHospital(existHospital);
            HospitalStaff hospitalStaff1 = hospitalStaffRepository.save(hospitalStaff);
            if (hospitalStaff1 == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User could not be created");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("User registration successfully");
        }catch (Exception e)
        {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/register/doctor")
    public ResponseEntity<?> register(@RequestBody Doctor doctor, @RequestParam String hospitalId)
    {
        try {
            Optional<User> existingUser = userRepository.findByUsername(doctor.getUsername());
            if(existingUser.isPresent())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already registered");
            }
            Optional<User> existEmail = userRepository.findByEmail(doctor.getEmail());
            if(existEmail.isPresent())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already exist");
            }
            Role existRole = roleRepository.findByName("ROLE_DOCTOR");
            if (existRole == null) {
                Role newrole = new Role("ROLE_DOCTOR");
                existRole = roleRepository.save(newrole);
            }
            Set<Role> roles = new HashSet<Role>();
            roles.add(existRole);
            String encodedPassword = passwordEncoder.encode(doctor.getPassword());
            doctor.setPassword(encodedPassword);
            doctor.setRoles(roles);
            Long id = Long.parseLong(hospitalId);
            Hospital matchedHospital = hospitalRepository.findByHospitalId(id);
            if (matchedHospital == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("The hospital is not found");
            }
            doctor.setHospital(matchedHospital);
            Doctor createdDoctor = doctorRepository.save(doctor);
            if (createdDoctor == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User could not be created");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("User registration successfully");
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register/patient")
    public ResponseEntity<?> register(@RequestBody Patient patient)
    {
        try {
            Optional<User> existingUser = userRepository.findByUsername(patient.getUsername());
            if(existingUser.isPresent())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is already registered");
            }
            Optional<User> existingEmail = userRepository.findByEmail(patient.getEmail());
            if(existingEmail.isPresent())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already exist");
            }
            Role existRole = roleRepository.findByName("ROLE_PATIENT");
            if (existRole == null) {
                Role newrole = new Role("ROLE_PATIENT");
                existRole = roleRepository.save(newrole);
            }
            Set<Role> roles = new HashSet<Role>();
            roles.add(existRole);
            String encodedPassword = passwordEncoder.encode(patient.getPassword());
            patient.setPassword(encodedPassword);
            patient.setRoles(roles);
            patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registration successfully");
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping(value = "/createHospital")
    public ResponseEntity<?> createHospital(@RequestBody Hospital hospital)
    {
        try {
            hospitalRepository.save(hospital);
            return ResponseEntity.status(HttpStatus.CREATED).body("Hospital created successfully");
        }catch(Exception e)
        {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/getHospitals")
    public ResponseEntity<?> getHospitals()
    {
        try {
            List<Hospital> hospitals = hospitalRepository.findAll();
            if(hospitals.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Hospitals in the Data base");
            }
            return ResponseEntity.ok(hospitals);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
