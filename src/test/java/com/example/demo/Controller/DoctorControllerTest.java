package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.AppoinmentRepository;
import com.example.demo.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorControllerTest {
    @Mock
    private AppoinmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorController doctorController;

    private Doctor doctor;
    private Hospital hospital;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        hospital = new Hospital();
        hospital.setHospitalId(1L);
        hospital.setHospitalName("Central Hospital");
        hospital.setHospitalLocation("Colombo");
        hospital.setHospitalType(HospitalType.PRIVATE);

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUsername("drjohn");
        doctor.setFirstName("John");
        doctor.setHospital(hospital);

        appointment = new Appointment();
        appointment.setId(10L);
        appointment.setDoctor(doctor);
        appointment.setStartTime(LocalDateTime.now());
        appointment.setEndTime(LocalDateTime.now().plusHours(1));
        appointment.setStatus(Status.BOOKED);
    }

    // ✅ Test getDoctorsHospital
    @Test
    void testGetDoctorsHospital_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        ResponseEntity<?> response = doctorController.getDoctorsHospital("1");

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> result = (Map<String, Object>) response.getBody();
        assertEquals("Central Hospital", result.get("hospitalName"));
    }

    @Test
    void testGetDoctorsHospital_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorController.getDoctorsHospital("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    // ✅ Test addAppointment
    @Test
    void testAddAppointment_Success() {
        when(appointmentRepository.findByDoctor(any(Doctor.class))).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        ResponseEntity<?> response = doctorController.AddAppointments(appointment);

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testAddAppointment_DoctorMissing() {
        Appointment invalid = new Appointment();
        ResponseEntity<?> response = doctorController.AddAppointments(invalid);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Doctor information is required"));
    }

    // ✅ Test getAppointments
    @Test
    void testGetAppointments_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(appointment));

        ResponseEntity<?> response = doctorController.getAppointments("1");

        assertEquals(200, response.getStatusCodeValue());
        List<Appointment> list = (List<Appointment>) response.getBody();
        assertEquals(1, list.size());
    }

    @Test
    void testGetAppointments_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = doctorController.getAppointments("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    // ✅ Test getDoctorByUserName
    @Test
    void testGetDoctorByUserName_Success() {
        when(doctorRepository.findByUsername("drjohn")).thenReturn(Optional.of(doctor));

        ResponseEntity<?> response = doctorController.getDoctorByUserName("drjohn");

        assertEquals(200, response.getStatusCodeValue());
        Doctor result = (Doctor) response.getBody();
        assertEquals("John", result.getFirstName());
    }

    @Test
    void testGetDoctorByUserName_NotFound() {
        when(doctorRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorController.getDoctorByUserName("invalid");

        assertEquals(404, response.getStatusCodeValue());
    }

    // ✅ Test markAsCompleted
    @Test
    void testMarkAsCompleted_Success() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appointment));

        ResponseEntity<?> response = doctorController.markAsCompleted("10");

        assertEquals(200, response.getStatusCodeValue());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testMarkAsCompleted_NotFound() {
        when(appointmentRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = doctorController.markAsCompleted("10");

        assertEquals(404, response.getStatusCodeValue());
    }

    // ✅ Test getBookedAppointments
    @Test
    void testGetBookedAppointments_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(appointment));

        ResponseEntity<?> response = doctorController.getBookedAppointments("1");

        assertEquals(200, response.getStatusCodeValue());
        List<Appointment> list = (List<Appointment>) response.getBody();
        assertEquals(1, list.size());
    }

    @Test
    void testGetBookedAppointments_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = doctorController.getBookedAppointments("1");

        assertEquals(404, response.getStatusCodeValue());
    }
}