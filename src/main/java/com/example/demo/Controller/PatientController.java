package com.example.demo.Controller;

import com.example.demo.Interfaces.INotificationService;
import com.example.demo.model.*;
import com.example.demo.repository.AppoinmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.PatientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;
    @Value("${frontend.url}")
    private String frontendUrl;
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;
    private final DoctorRepository doctorRepository;
    private final AppoinmentRepository appoinmentRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final List<INotificationService> notoficationServices;
    public PatientController(DoctorRepository doctorRepository,AppoinmentRepository appoinmentRepository,
                             PatientRepository patientRepository,HospitalRepository hospitalRepository,
                             List<INotificationService> notificationServices)
    {
        this.appoinmentRepository = appoinmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.notoficationServices = notificationServices;
    }

    @GetMapping("/getDoctors")
    public ResponseEntity<?> getDoctors()
    {
        try {
            List<Doctor> doctors = doctorRepository.findAll();

            if (doctors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No doctors found in the database");
            }
            // 👇 Create a new list to hold doctor + hospital info
            List<Map<String, Object>> doctorList = new ArrayList<>();

            for (Doctor doctor : doctors) {
                Map<String, Object> doctorData = new HashMap<>();
                doctorData.put("doctorId", doctor.getId());
                doctorData.put("firstName", doctor.getFirstName());
                doctorData.put("lastName", doctor.getLastName());
                doctorData.put("specialization", doctor.getSpecialization());
                doctorData.put("username", doctor.getUsername());
                doctorData.put("email", doctor.getEmail());

                // 👇 Manually include hospital info
                Hospital hospital = doctor.getHospital();
                if (hospital != null) {
                    Map<String, Object> hospitalData = new HashMap<>();
                    hospitalData.put("hospitalId", hospital.getHospitalId());
                    hospitalData.put("hospitalName", hospital.getHospitalName());
                    hospitalData.put("hospitalLocation", hospital.getHospitalLocation());
                    hospitalData.put("hospitalType", hospital.getHospitalType().toString());
                    doctorData.put("hospital", hospitalData);
                }

                doctorList.add(doctorData);
            }
            return ResponseEntity.ok(doctorList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching doctors: " + e.getMessage());
        }
    }


    @GetMapping("/bookAppointment")
    @Transactional
    public ResponseEntity<?> BookingAppointment(@RequestParam String appointmentID,@RequestParam String patientID)
    {
        try {
            Optional<Appointment> appointment = appoinmentRepository.findById(Long.parseLong(appointmentID));
            if (appointment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment is not found");
            }
            if (appointment.get().getStatus().toString().equals("BOOKED")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("The appointment is already booked");
            }
            Optional<Patient> patient = patientRepository.findById(Long.parseLong(patientID));
            if (patient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient is not found");
            }
            Appointment updateAppointment = appointment.get();
            updateAppointment.setPatient(patient.get());
            updateAppointment.setStatus(Status.BOOKED);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");
            String body = "Dear " + patient.get().getFirstName() + ",\n\n" +
                    "We are pleased to inform you that your appointment has been successfully booked.\n\n" +
                    "🩺 Doctor: " + appointment.get().getDoctor().getFirstName() + " " + appointment.get().getDoctor().getLastName() + "\n" +
                    "📅 Date & Time: " + appointment.get().getStartTime().format(formatter) + " - " +
                    appointment.get().getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")) + "\n" +
                    "🏥 Room: " + (appointment.get().getRoomLocation() != null && !appointment.get().getRoomLocation().isEmpty()
                    ? appointment.get().getRoomLocation()
                    : "Not Assigned") + "\n\n" +
                    "Please arrive 10 minutes early and bring any relevant documents or reports.\n\n" +
                    "Thank you for choosing our hospital!\n\n" +
                    "Best regards,\n" +
                    "Your Hospital Team";
            String subject = "✅ Appointment Confirmed: Your Booking with Dr. "
                    + appointment.get().getDoctor().getFirstName() + " "
                    + appointment.get().getDoctor().getLastName();
            updateAppointment.setBookedTime(LocalDateTime.now());
            appoinmentRepository.save(updateAppointment);
            for (INotificationService service : notoficationServices) {
                service.sendNotification(patient.get().getEmail(), subject, body);
            }
            return ResponseEntity.ok("Booking Successful");
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getPatient")
    public ResponseEntity<?> getPatientById(@RequestParam String username)
    {
        Optional<Patient> matchedPatient = patientRepository.findByUsername(username);
        if(matchedPatient.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient is not found");
        }
        return ResponseEntity.ok(matchedPatient.get());
    }

    @GetMapping("/getBookedAppointments")
    public ResponseEntity<?> getBookedAppointmentsByPatient(@RequestParam String patientId)
    {
        try {
            Optional<Patient> matchedPatient = patientRepository.findById(Long.parseLong(patientId));
            if (matchedPatient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient is not found");
            }
            List<Appointment> matchedAppointments = appoinmentRepository.findByPatient(matchedPatient.get());
            if (matchedAppointments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Appointments for this patient");
            }
            List<Map<String, Object>> AppointmentList = new ArrayList<>();
            for (Appointment appointment : matchedAppointments) {
                Map<String, Object> appointmentObject = new HashMap<>();
                appointmentObject.put("id", appointment.getId());
                appointmentObject.put("doctorFullName", appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
                appointmentObject.put("startTime", appointment.getStartTime());
                appointmentObject.put("endTime", appointment.getEndTime());
                appointmentObject.put("roomLocation", appointment.getRoomLocation());
                appointmentObject.put("status", appointment.getStatus());
                appointmentObject.put("BookedAt",appointment.getBookedTime());
                Hospital hospital = hospitalRepository.findByDoctors(appointment.getDoctor());
                appointmentObject.put("HospitalName", hospital.getHospitalName());
                AppointmentList.add(appointmentObject);
            }
            return ResponseEntity.ok(AppointmentList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/payment/create-checkout-session")
    public ResponseEntity<?> payAppointment(@RequestBody Map<String, Object> data) {
        try {
            Stripe.apiKey = stripeApiKey;
            System.out.println("✅ Stripe webhook triggered successfully");

            Long appointmentId = Long.parseLong(data.get("appointmentId").toString());
            Optional<Appointment> matchedAppointment = appoinmentRepository.findById(appointmentId);
            if (matchedAppointment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found the Appointment");
            }

            String doctorName = matchedAppointment.get().getDoctor().getFirstName() + " "
                    + matchedAppointment.get().getDoctor().getLastName();

            Long patientId = Long.parseLong(data.get("patientID").toString());
            double amountInUSDInDouble = Double.parseDouble(data.get("amount").toString());
            long amountInUSD = (long) amountInUSDInDouble;
            double amountInRSInDouble = Double.parseDouble(data.get("amountInRs").toString());
            long amountInRS = (long) amountInRSInDouble;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/patient/successfulPayment")
                    .setCancelUrl(frontendUrl + "/payment-cancelled")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(amountInUSD * 100) // Convert USD to cents
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Appointment with Dr. " + doctorName)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("appointmentID", appointmentId.toString())
                    .putMetadata("patientID", patientId.toString())
                    // 👇 Important line to fix deserialization
                    .addExpand("line_items")
                    .addExpand("customer")
                    .build();

            Session session = Session.create(params);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("url", session.getUrl());
            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }


    @PostMapping("/payment/webhook")
    public ResponseEntity<?> handleStripeEvent(@RequestHeader("Stripe-Signature") String sigHeader,
                                               @RequestBody String payload) {
        Event event = null;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Stripe webhook is called");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }

        try {
            if ("checkout.session.completed".equals(event.getType())) {
                System.out.println("Session is completed");
                EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

                Session session = null;
                if (dataObjectDeserializer.getObject().isPresent()) {
                    session = (Session) dataObjectDeserializer.getObject().get();
                } else {
                    // ⚠️ Fallback: fetch full session by ID
                    System.out.println("⚠️ Stripe session data could not be automatically deserialized.");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(payload);
                    String sessionId = node.path("data").path("object").path("id").asText();

                    if (sessionId != null && !sessionId.isEmpty()) {
                        Stripe.apiKey = stripeApiKey;
                        session = Session.retrieve(sessionId);
                        System.out.println("✅ Fetched session manually using Session.retrieve()");
                    } else {
                        return ResponseEntity.ok("Session data not deserialized");
                    }
                }

                if (session != null) {
                    System.out.println("Session is not null");

                    String appointmentIdStr = session.getMetadata().get("appointmentID");
                    String patientIdStr = session.getMetadata().get("patientID");

                    if (appointmentIdStr == null || patientIdStr == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing metadata fields");
                    }

                    Long appointmentId = Long.parseLong(appointmentIdStr);
                    Long patientId = Long.parseLong(patientIdStr);

                    Optional<Patient> matchedPatient = patientRepository.findById(patientId);
                    if (matchedPatient.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient is not found");
                    }

                    appoinmentRepository.findById(appointmentId).ifPresent(appointment -> {
                        System.out.println("Appointments");
                        appointment.setStatus(Status.BOOKED);
                        appointment.setPatient(matchedPatient.get());
                        appointment.setBookedTime(LocalDateTime.now());
                        appoinmentRepository.save(appointment);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");
                        String body = "Dear " + matchedPatient.get().getFirstName() + ",\n\n" +
                                "We are pleased to inform you that your appointment has been successfully booked.\n\n" +
                                "🩺 Doctor: " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName() + "\n" +
                                "📅 Date & Time: " + appointment.getStartTime().format(formatter) + " - " +
                                appointment.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")) + "\n" +
                                "🏥 Room: " + (appointment.getRoomLocation() != null && !appointment.getRoomLocation().isEmpty()
                                ? appointment.getRoomLocation() : "Not Assigned") + "\n\n" +
                                "Please arrive 10 minutes early and bring any relevant documents or reports.\n\n" +
                                "Thank you for choosing our hospital!\n\n" +
                                "Best regards,\nYour Hospital Team";

                        String subject = "✅ Appointment Confirmed: Your Booking with Dr. "
                                + appointment.getDoctor().getFirstName() + " "
                                + appointment.getDoctor().getLastName();

                        for (INotificationService service : notoficationServices) {
                            service.sendNotification(matchedPatient.get().getEmail(), subject, body);
                        }
                    });
                }
            }

            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/GetCompletedAppointments")
    public ResponseEntity<?> getCompletedAppointmentsByPatientID(@RequestParam String patientID)
    {

        if(patientID==null)
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The patientID is not in the request params");
        }
        try {
            Optional<Patient> matchedPatient = patientRepository.findById(Long.parseLong(patientID));
            if (matchedPatient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The patient is not found");
            }
            Patient patientObject = matchedPatient.get();
            List<Appointment> matchedAppointments = appoinmentRepository.findByPatient(patientObject);
            if (matchedAppointments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Their are not appointments for this patient");
            }
            List<Appointment> completedAppointments = new ArrayList<>();
            List<Appointment> bookedAppointments = new ArrayList<>();
            for (Appointment appointment : matchedAppointments) {
                if (appointment.getStatus() == Status.COMPLETED) {
                    completedAppointments.add(appointment);
                }else if(appointment.getStatus()==Status.BOOKED)
                {
                    bookedAppointments.add(appointment);
                }
            }
            List<Appointment> completedAppointmentsByDesc = completedAppointments.stream().sorted((a1,a2)->a2.getCompleteTime().compareTo(a1.getCompleteTime()))
                    .collect(Collectors.toList());
            Map<String,Object> response = new HashMap<>();
            response.put("completedAppointmentsByDesc",completedAppointmentsByDesc);
            response.put("bookedAppointments",bookedAppointments);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAvailableAppointments")
    public ResponseEntity<?> getAvailableAppointments(@RequestParam String doctorId)
    {
        if(doctorId==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The doctorId is missing in the request");
        }
        try {
            Long doctorID = Long.parseLong(doctorId);
            Optional<Doctor> matchedDoctor = doctorRepository.findById(doctorID);
            if (matchedDoctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The doctor is not found");
            }
            Doctor doctor = matchedDoctor.get();
            List<Appointment> doctorAppointments = appoinmentRepository.findByDoctor(doctor);
            List<Appointment> availableAppointments = new ArrayList<>();
            for (Appointment appointment : doctorAppointments) {
                if (appointment.getStatus() != Status.BOOKED &&
                        appointment.getStatus() != Status.COMPLETED &&
                        appointment.getStatus() != Status.CANCEL) {
                    availableAppointments.add(appointment);
                }
            }
            return ResponseEntity.ok(availableAppointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server error");
        }
    }
}
