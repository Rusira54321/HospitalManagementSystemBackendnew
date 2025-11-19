package com.example.demo.Controller;

import com.example.demo.Interfaces.INotificationService;
import com.example.demo.model.*;
import com.example.demo.repository.AppoinmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.services.GetPatientMedicalRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PatientControllerTest {

    @InjectMocks
    private PatientController patientController;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppoinmentRepository appoinmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private List<INotificationService> notificationServices;

    @Mock
    private GetPatientMedicalRecords getPatientMedicalRecords;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationServices = new ArrayList<>();
        patientController = new PatientController(doctorRepository, appoinmentRepository, patientRepository, hospitalRepository, notificationServices,
                getPatientMedicalRecords);
    }

    @Test
    void testGetDoctors_WhenDoctorsExist() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setUsername("johndoe");
        doctor.setEmail("john@example.com");
        doctor.setSpecialization("Cardiology");

        Hospital hospital = new Hospital();
        hospital.setHospitalId(1L);
        hospital.setHospitalName("City Hospital");
        hospital.setHospitalLocation("Downtown"); // ⚡ Added
        hospital.setHospitalType(HospitalType.PRIVATE); // ⚡ Added
        doctor.setHospital(hospital);

        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        ResponseEntity<?> response = patientController.getDoctors();

        assertEquals(200, response.getStatusCodeValue());
        List<?> doctors = (List<?>) response.getBody();
        assertNotNull(doctors);
        assertEquals(1, doctors.size());
        Map<String, Object> doctorData = (Map<String, Object>) doctors.get(0);
        assertEquals("John", doctorData.get("firstName"));
        assertEquals("City Hospital", ((Map<String, Object>) doctorData.get("hospital")).get("hospitalName"));
    }

    @Test
    void testBookingAppointment_Success() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Status.AVAILABLE);
        appointment.setStartTime(LocalDateTime.now());
        appointment.setEndTime(LocalDateTime.now().plusHours(1));
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        appointment.setDoctor(doctor);

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setEmail("alice@example.com");

        when(appoinmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        ResponseEntity<?> response = patientController.BookingAppointment("1", "1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Booking Successful", response.getBody());
        assertEquals(Status.BOOKED, appointment.getStatus());
        assertEquals(patient, appointment.getPatient());
    }

    @Test
    void testGetPatientById_WhenPatientExists() {
        Patient patient = new Patient();
        patient.setUsername("alice");
        when(patientRepository.findByUsername("alice")).thenReturn(Optional.of(patient));

        ResponseEntity<?> response = patientController.getPatientById("alice");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(patient, response.getBody());
    }

    @Test
    void testGetBookedAppointmentsByPatient_WhenAppointmentsExist() {
        Patient patient = new Patient();
        patient.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStartTime(LocalDateTime.now());
        appointment.setEndTime(LocalDateTime.now().plusHours(1));
        appointment.setStatus(Status.BOOKED);

        Hospital hospital = new Hospital();
        hospital.setHospitalName("City Hospital");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appoinmentRepository.findByPatient(patient)).thenReturn(List.of(appointment));
        when(hospitalRepository.findByDoctors(doctor)).thenReturn(hospital);

        ResponseEntity<?> response = patientController.getBookedAppointmentsByPatient("1");

        assertEquals(200, response.getStatusCodeValue());
        List<?> appointments = (List<?>) response.getBody();
        assertEquals(1, appointments.size());
        Map<String, Object> appointmentData = (Map<String, Object>) appointments.get(0);
        assertEquals("John Doe", appointmentData.get("doctorFullName"));
        assertEquals("City Hospital", appointmentData.get("HospitalName"));
    }

    @Test
    void testGetCompletedAppointmentsByPatientID_Success() {
        Patient patient = new Patient();
        patient.setId(1L);

        Appointment completed = new Appointment();
        completed.setStatus(Status.COMPLETED);
        completed.setCompleteTime(LocalDateTime.now().minusDays(1));

        Appointment booked = new Appointment();
        booked.setStatus(Status.BOOKED);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appoinmentRepository.findByPatient(patient)).thenReturn(List.of(completed, booked));

        ResponseEntity<?> response = patientController.getCompletedAppointmentsByPatientID("1");

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        List<?> completedAppointments = (List<?>) body.get("completedAppointmentsByDesc");
        List<?> bookedAppointments = (List<?>) body.get("bookedAppointments");

        assertEquals(1, completedAppointments.size());
        assertEquals(1, bookedAppointments.size());
    }

}
