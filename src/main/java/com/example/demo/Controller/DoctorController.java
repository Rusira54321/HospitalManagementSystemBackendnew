package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.lang.annotation.Documented;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController
{
    private final AppoinmentRepository appoinmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalStaffRepository hospitalStaffRepository;
    private final MedicalRecordsRepository medicalRecordsRepository;
    private final PatientRepository patientRepository;
    public DoctorController(AppoinmentRepository appoinmentRepository, DoctorRepository doctorRepository
    , MedicalRecordsRepository medicalRecordsRepository,PatientRepository patientRepository,
                            HospitalStaffRepository hospitalStaffRepository,HospitalRepository hospitalRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
        this.doctorRepository = doctorRepository;
        this.medicalRecordsRepository = medicalRecordsRepository;
        this.patientRepository = patientRepository;
        this.hospitalStaffRepository = hospitalStaffRepository;
        this.hospitalRepository = hospitalRepository;
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
    @Transactional
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
            doctorRepository.findByIdWithLock(doctor.getId());
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
    @Transactional
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

    @PostMapping("/AddMedicalRecords")
    public ResponseEntity<?> addMedicalRecords(@RequestParam String patientID,
                                               @RequestBody MedicalRecords record)
    {
        if(patientID==null || record==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required fields are missing");
        }
        try {
            Optional<Patient> existPatient = patientRepository.findById(Long.parseLong(patientID));
            if (existPatient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The patient is not found");
            }
            Patient existPatientObject = existPatient.get();
            MedicalRecords existMedicalRecords = existPatientObject.getMedicalRecords();
            if (existMedicalRecords != null) {
                existPatientObject.setMedicalRecords(null);
                patientRepository.save(existPatientObject);
            }
            existPatientObject.setMedicalRecords(record);
            patientRepository.save(existPatientObject);
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully added medical record");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getPatients")
    public ResponseEntity<?> getPatientById(@RequestParam String patientId)
    {
        try {
            Optional<Patient> matchedPatient = patientRepository.findById(Long.parseLong(patientId));
            if (matchedPatient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient is not found");
            }
            Patient patientObject = matchedPatient.get();
            return ResponseEntity.ok(patientObject);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getMedicalRecords")
    public  ResponseEntity<?> getMedicalRecords(@RequestParam String patientId)
    {
        if(patientId==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required");
        }
        try {
            Optional<Patient> matchedPatient = patientRepository.findById(Long.parseLong(patientId));
            if (matchedPatient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The patient is not found");
            }
            Patient matchedPatientObject = matchedPatient.get();
            MedicalRecords medicalRecords = matchedPatientObject.getMedicalRecords();
            return ResponseEntity.ok(medicalRecords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getBookedCompletedAppointments")
    public ResponseEntity<?> getBookedCompletedAppointments(@RequestParam String patientID)
    {
        if(patientID==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("All fields are required");
        }
        try{
            Optional<Patient> matchedPatient = patientRepository.findById(Long.parseLong(patientID));
            if(matchedPatient.isEmpty())
            {
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("The patient is not found");
            }
            Patient patientObject = matchedPatient.get();
            List<Appointment> appointments = appoinmentRepository.findByPatient(patientObject);
            List<Appointment> bookedAppointments = new ArrayList<>();
            List<Appointment> completedAppointments = new ArrayList<>();
            for(Appointment appointment:appointments)
            {
                if(appointment.getStatus()==Status.COMPLETED)
                {
                    completedAppointments.add(appointment);
                }
                else if(appointment.getStatus()==Status.BOOKED)
                {
                    bookedAppointments.add(appointment);
                }
            }
            List<Appointment> sortedCompletedlist = completedAppointments.stream().sorted((a1,a2)->a2.getCompleteTime().compareTo(a1.getCompleteTime()))
                    .collect(Collectors.toList());
            Map<String,Object> response = new HashMap<>();
            response.put("bookedAppointments",bookedAppointments);
            response.put("completedAppointments",sortedCompletedlist);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/getSecretaries")
    public ResponseEntity<?> getSecretaries(@RequestParam String doctorUserName)
    {
        if(doctorUserName==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The doctor user name is " +
                    "not in the request");
        }
        try {
            Optional<Doctor> doctor = doctorRepository.findByUsername(doctorUserName);
            if (doctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Doctor is not found");
            }
            Doctor doctorObject = doctor.get();
            Hospital hospital = hospitalRepository.findByDoctors(doctorObject);
            if (hospital == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Hospital is not found");
            }
            List<HospitalStaff> hospitalStaffs = hospitalStaffRepository.findByHospital(hospital);
            List<HospitalStaff> secretaries = new ArrayList<>();
            for (HospitalStaff hospitalStaff : hospitalStaffs) {
                boolean isHospitalStaff = hospitalStaff.getRoles().stream().anyMatch(role -> "ROLE_HOSPITALSTAFF".equals(role.getName()));
                if (isHospitalStaff) {
                    secretaries.add(hospitalStaff);
                }
            }
            return ResponseEntity.ok(secretaries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server error");
        }
    }

    @GetMapping("/addSecretary")
    public ResponseEntity<?> addSecretaries(@RequestParam String DoctorUserName
            ,@RequestParam String secretaryId)
    {
            if(DoctorUserName==null || secretaryId ==null)
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DoctorUsername or secretaryId is missing in the" +
                        "request");
            }
            try {
                Optional<Doctor> doctor = doctorRepository.findByUsername(DoctorUserName);
                if (doctor.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The doctor is not found");
                }
                Doctor doctorObject = doctor.get();
                Optional<HospitalStaff> hospitalStaff = hospitalStaffRepository.findById(Long.parseLong(secretaryId));
                if (hospitalStaff.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Hospital Staff is not found");
                }
                HospitalStaff hospitalStaffObject = hospitalStaff.get();
                doctorObject.setHospitalStaff(hospitalStaffObject);
                doctorRepository.save(doctorObject);
                return ResponseEntity.ok("Successfully add the Secretary");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
    }

    @GetMapping("/getSecretary")
    public ResponseEntity<?> getDoctorsSecretary(@RequestParam String doctorUserName)
    {
        if(doctorUserName.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DoctorUserName is " +
                    "missing in the request");
        }
        try {
            Optional<Doctor> doctor = doctorRepository.findByUsername(doctorUserName);
            if (doctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The doctor is not found");
            }
            Doctor doctorObject = doctor.get();
            HospitalStaff secretary = doctorObject.getHospitalStaff();
            return ResponseEntity.ok(secretary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}