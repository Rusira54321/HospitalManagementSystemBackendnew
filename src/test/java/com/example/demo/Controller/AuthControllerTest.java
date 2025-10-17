package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HospitalStaffRepository hospitalStaffRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------- HospitalStaff Registration --------------------
    @Test
    void testRegisterHospitalStaff_Success() {
        HospitalStaff staff = new HospitalStaff();
        staff.setUsername("staff1");
        staff.setEmail("staff1@example.com");
        staff.setPassword("password");

        Hospital hospital = new Hospital();
        hospital.setHospitalId(1L);

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("staff1@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(hospitalRepository.findByHospitalId(1L)).thenReturn(hospital);
        when(hospitalStaffRepository.save(any(HospitalStaff.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = authController.register(staff, "1", "admin");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registration successfully", response.getBody());
    }

    @Test
    void testRegisterHospitalStaff_UserAlreadyExists() {
        HospitalStaff staff = new HospitalStaff();
        staff.setUsername("staff1");

        when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(new HospitalStaff()));

        ResponseEntity<?> response = authController.register(staff, "1", "admin");
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User is already registered", response.getBody());
    }

    // -------------------- Doctor Registration --------------------
    @Test
    void testRegisterDoctor_Success() {
        Doctor doctor = new Doctor();
        doctor.setUsername("doc1");
        doctor.setEmail("doc1@example.com");
        doctor.setPassword("password");

        Hospital hospital = new Hospital();
        hospital.setHospitalId(1L);

        when(userRepository.findByUsername("doc1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("doc1@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_DOCTOR")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(hospitalRepository.findByHospitalId(1L)).thenReturn(hospital);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = authController.register(doctor, "1");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registration successfully", response.getBody());
    }

    @Test
    void testRegisterDoctor_UserAlreadyExists() {
        Doctor doctor = new Doctor();
        doctor.setUsername("doc1");

        when(userRepository.findByUsername("doc1")).thenReturn(Optional.of(new Doctor()));

        ResponseEntity<?> response = authController.register(doctor, "1");
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User is already registered", response.getBody());
    }

    // -------------------- Patient Registration --------------------
    @Test
    void testRegisterPatient_Success() {
        Patient patient = new Patient();
        patient.setUsername("patient1");
        patient.setEmail("patient1@example.com");
        patient.setPassword("password");

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("patient1@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_PATIENT")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArguments()[0]);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = authController.register(patient);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registration successfully", response.getBody());
    }

    @Test
    void testRegisterPatient_UserAlreadyExists() {
        Patient patient = new Patient();
        patient.setUsername("patient1");

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(new Patient()));

        ResponseEntity<?> response = authController.register(patient);
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("User is already registered", response.getBody());
    }

    // -------------------- Create Hospital --------------------
    @Test
    void testCreateHospital_Success() {
        Hospital hospital = new Hospital();
        hospital.setHospitalName("City Hospital");

        when(hospitalRepository.save(any(Hospital.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<?> response = authController.createHospital(hospital);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Hospital created successfully", response.getBody());
    }

    // -------------------- Get Hospitals --------------------
    @Test
    void testGetHospitals_Success() {
        Hospital hospital = new Hospital();
        hospital.setHospitalName("City Hospital");

        when(hospitalRepository.findAll()).thenReturn(List.of(hospital));

        ResponseEntity<?> response = authController.getHospitals();
        assertEquals(200, response.getStatusCodeValue());
        List<?> hospitals = (List<?>) response.getBody();
        assertEquals(1, hospitals.size());
    }

    @Test
    void testGetHospitals_NotFound() {
        when(hospitalRepository.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = authController.getHospitals();
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("No Hospitals in the Data base", response.getBody());
    }
}