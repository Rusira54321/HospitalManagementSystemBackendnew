package com.example.demo.Controller;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Hospital;
import com.example.demo.model.Status;
import com.example.demo.repository.AppoinmentRepository;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.lang.annotation.Documented;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController
{
    private final AppoinmentRepository appoinmentRepository;
    private final DoctorRepository doctorRepository;
    public DoctorController(AppoinmentRepository appoinmentRepository,DoctorRepository doctorRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/getHospital")
    public ResponseEntity<?> getDoctorsHospital(@RequestParam String doctorID)
    {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(Long.parseLong(doctorID));
            if (doctor.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            Hospital doctorHospital = doctor.get().getHospital();
            Map<String, Object> hospitalData = new HashMap<>();
            hospitalData.put("hospitalId", doctorHospital.getHospitalId());
            hospitalData.put("hospitalName", doctorHospital.getHospitalName());
            hospitalData.put("hospitalLocation", doctorHospital.getHospitalLocation());
            hospitalData.put("hospitalType", doctorHospital.getHospitalType().toString());

            return ResponseEntity.ok(hospitalData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/addAppointment")
    public ResponseEntity<?> AddAppointments(@RequestBody Appointment appointment)
    {
        try {
            Doctor doctor = appointment.getDoctor();
            if (doctor == null || doctor.getId() == null) {
                return ResponseEntity.badRequest().body("Doctor information is required.");
            }
            LocalDateTime endtime = appointment.getEndTime();
            LocalDateTime startTime = appointment.getStartTime();
            Status status = appointment.getStatus();
            if (endtime == null || startTime == null || status == null) {
                return ResponseEntity.badRequest().body("ALL Fields are required");
            }
            List<Appointment> appointments = appoinmentRepository.findByDoctor(doctor);
            LocalDateTime newStart = appointment.getStartTime();
            LocalDateTime newEnd = appointment.getEndTime();
            for (Appointment existing : appointments) {
                LocalDateTime existingStart = existing.getStartTime();
                LocalDateTime existingEnd = existing.getEndTime();
                boolean overlaps = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
                if (overlaps) {
                    return ResponseEntity.badRequest().body("This doctor already has an appointment during the selected time.");
                }
            }
            appointment.setCreateTime(LocalDateTime.now());
            Appointment saved = appoinmentRepository.save(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAppointments")
    public ResponseEntity<?> getAppointments(@RequestParam String doctorId)
    {
        try {
            Long Id = Long.parseLong(doctorId);
            Optional<Doctor> doctor1 = doctorRepository.findById(Id);
            List<Appointment> appointments = appoinmentRepository.findByDoctor(doctor1.get());
            if(appointments.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The doctors has not any appointments");
            }
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getDoctor")
    public ResponseEntity<?> getDoctorByUserName(@RequestParam String username)
    {
        try {
            Optional<Doctor> doctor = doctorRepository.findByUsername(username);
            if(doctor.isEmpty())
            {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(doctor.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getBookedAppointments")
    public ResponseEntity<?> getBookedAppointments(@RequestParam String doctorId)
    {
        try {
            Optional<Doctor> matchedDoctor = doctorRepository.findById(Long.parseLong(doctorId));
            Doctor doctor = matchedDoctor.get();
            List<Appointment> appointmentList = appoinmentRepository.findByDoctor(doctor);
            if (appointmentList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The doctors has not any appointments");
            }
            List<Appointment> bookedAppointments = new ArrayList<>();
            for (Appointment oneappointment : appointmentList) {
                if (oneappointment.getStatus() == Status.BOOKED) {
                    bookedAppointments.add(oneappointment);
                }
            }
            return ResponseEntity.ok(bookedAppointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/markAsCompleted")
    public ResponseEntity<?> markAsCompleted(@RequestParam String appointmentId)
    {
        if(appointmentId==null)
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The appointmentId is not found in the request");
        }
        try {
            Optional<Appointment> appointment = appoinmentRepository.findById(Long.parseLong(appointmentId));
            if (appointment.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The appointment is not found");
            }
            Appointment matchedAppointment = appointment.get();
            matchedAppointment.setStatus(Status.COMPLETED);
            matchedAppointment.setCompleteTime(LocalDateTime.now());
            appoinmentRepository.save(matchedAppointment);
            return ResponseEntity.ok("Mark as completed");
        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}