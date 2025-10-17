package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {
    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HospitalStaffRepository hospitalStaffRepository;

    @Mock
    private AppoinmentRepository appoinmentRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPatients() {
        List<Patient> mockPatients = List.of(new Patient(), new Patient());
        when(patientRepository.findAll()).thenReturn(mockPatients);

        ResponseEntity<?> response = adminController.getAllPatients();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPatients, response.getBody());
    }

    @Test
    void testGetAllDoctors() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setEmail("john@example.com");
        doctor.setSpecialization("Cardiology");

        Hospital hospital = new Hospital();
        hospital.setHospitalName("City Hospital");
        hospital.setHospitalLocation("Colombo");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(hospitalRepository.findByDoctors(doctor)).thenReturn(hospital);

        ResponseEntity<?> response = adminController.getAllDoctors();
        List<Map<String, Object>> doctorsList = (List<Map<String, Object>>) response.getBody();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(doctorsList);
        assertEquals("John Doe", doctorsList.get(0).get("Full name"));
        assertEquals("City Hospital", doctorsList.get(0).get("HospitalName"));
    }

    @Test
    void testDeletePatientSuccess() {
        Patient patient = new Patient();
        patient.setId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appoinmentRepository.findByPatient(patient)).thenReturn(List.of(appointment));

        ResponseEntity<?> response = adminController.deletePatients(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("The patient is deleted", response.getBody());

        verify(appoinmentRepository).deleteById(appointment.getId());
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void testDeletePatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = adminController.deletePatients(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Patient is not found", response.getBody());
    }

    @Test
    void testDeleteDoctorSuccess() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appoinmentRepository.findByDoctor(doctor)).thenReturn(List.of(appointment));

        ResponseEntity<?> response = adminController.deleteDoctor(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("The doctors is deleted", response.getBody());

        verify(appoinmentRepository).deleteById(appointment.getId());
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void testDeleteDoctorNotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = adminController.deleteDoctor(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Doctor is not found", response.getBody());
    }

    @Test
    void testGetAllHospitals() {
        Hospital hospital = new Hospital();
        hospital.setHospitalName("City Hospital");

        when(hospitalRepository.findAll()).thenReturn(List.of(hospital));

        ResponseEntity<?> response = adminController.getAllHospitals();

        assertEquals(200, response.getStatusCodeValue());
        List<Hospital> hospitals = (List<Hospital>) response.getBody();
        assertEquals(1, hospitals.size());
        assertEquals("City Hospital", hospitals.get(0).getHospitalName());
    }

}