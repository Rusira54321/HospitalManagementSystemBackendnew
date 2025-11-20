package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.*;
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalStaffRepository hospitalStaffRepository;
    private final AppoinmentRepository appoinmentRepository;
    private final getTotalPatients getTotalPatients;
    private final GetTotalDoctors getTotalDoctors;
    private final GetTotalAppointments getTotalAppointments;
    private final GetTotalHospitalStaffCount getTotalHospitalStaffCount;
    private final WeeklySpecializationService weeklySpecializationService;
    private final GetAppointmentsWithMonth getAppointmentsWithMonth;
    AdminController(PatientRepository patientRepository, DoctorRepository doctorRepository
    , HospitalRepository hospitalRepository, HospitalStaffRepository hospitalStaffRepository,
                    AppoinmentRepository appoinmentRepository,
                    getTotalPatients getTotalPatients, GetTotalDoctors getTotalDoctors,
                    GetTotalAppointments getTotalAppointments,
                    GetTotalHospitalStaffCount getTotalHospitalStaffCount,
                    WeeklySpecializationService weeklySpecializationService,
                    GetAppointmentsWithMonth getAppointmentsWithMonth)
    {
        this.getAppointmentsWithMonth = getAppointmentsWithMonth;
        this.weeklySpecializationService = weeklySpecializationService;
        this.getTotalHospitalStaffCount = getTotalHospitalStaffCount;
        this.getTotalAppointments = getTotalAppointments;
        this.getTotalDoctors = getTotalDoctors;
        this.getTotalPatients = getTotalPatients;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.hospitalStaffRepository = hospitalStaffRepository;
        this.appoinmentRepository = appoinmentRepository;
    }

    @GetMapping("/getAllPatients")
    public ResponseEntity<?> getAllPatients()
    {
        try {
            List<Patient> allPatients = patientRepository.findAll();
            return ResponseEntity.ok(allPatients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAllDoctors")
    public ResponseEntity<?> getAllDoctors()
    {
        try{
            List<Doctor> allDoctors = doctorRepository.findAll();
            List<Map<String,Object>> Doctors = new ArrayList<>();
            for(Doctor doctor:allDoctors)
            {
                Map<String,Object> singleDoctor = new HashMap<>();
                Hospital hospital = hospitalRepository.findByDoctors(doctor);
                singleDoctor.put("id",doctor.getId());
                singleDoctor.put("Email",doctor.getEmail());
                singleDoctor.put("Specialization",doctor.getSpecialization());
                singleDoctor.put("Full name",doctor.getFirstName()+" "+ doctor.getLastName());
                singleDoctor.put("HospitalName",hospital.getHospitalName());
                singleDoctor.put("HospitalLocation",hospital.getHospitalLocation());
                Doctors.add(singleDoctor);
            }
            return ResponseEntity.ok(Doctors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAllHospitals")
    public ResponseEntity<?> getAllHospitals()
    {
        try{
            List<Hospital> allHospitals = hospitalRepository.findAll();
            return ResponseEntity.ok(allHospitals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getAllHealthCareManagers")
    public ResponseEntity<?> getAllHealthCareManagers()
    {
        try{
            List<HospitalStaff> allHospitalStaff = hospitalStaffRepository.findAll();
            List<Map<String,Object>> AllhealthcareManagers= new ArrayList<>();
            for(HospitalStaff staffMember:allHospitalStaff)
            {
                    boolean isHManager = staffMember.getRoles().stream().anyMatch(role ->"ROLE_HEALTHCAREMANAGER".equals(role.getName()));
                    if(isHManager){
                        Hospital hospital = hospitalRepository.findByhospitalStaffs(staffMember);
                        Map<String,Object> healthManager = new HashMap<>();
                        healthManager.put("id",staffMember.getId());
                        healthManager.put("hospitalName",hospital.getHospitalName());
                        healthManager.put("hospitalLocation",hospital.getHospitalLocation());
                        healthManager.put("Email",staffMember.getEmail());
                        healthManager.put("Full name",staffMember.getFirstName()+" "+staffMember.getLastName());
                        AllhealthcareManagers.add(healthManager);
                    }
            }
            return ResponseEntity.ok(AllhealthcareManagers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/GetAllHospitalStaff")
    public ResponseEntity<?> getAllHospitalStaff()
    {
        try{
            List<HospitalStaff> allHospitalStaff = hospitalStaffRepository.findAll();
            List<Map<String,Object>> AllhospitalStaffs= new ArrayList<>();
            for(HospitalStaff staffMember:allHospitalStaff)
            {
                boolean isHospitalStaff = staffMember.getRoles().stream().anyMatch(role->"ROLE_HOSPITALSTAFF".equals(role.getName()));
                if(isHospitalStaff){
                    Hospital hospital = hospitalRepository.findByhospitalStaffs(staffMember);
                    Map<String,Object> hStaff = new HashMap<>();
                    hStaff.put("id",staffMember.getId());
                    hStaff.put("hospitalName",hospital.getHospitalName());
                    hStaff.put("Email",staffMember.getEmail());
                    hStaff.put("hospitalLocation",hospital.getHospitalLocation());
                    hStaff.put("Full name",staffMember.getFirstName()+" "+staffMember.getLastName());
                    AllhospitalStaffs.add(hStaff);
                }
            }
            return ResponseEntity.ok(AllhospitalStaffs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletePatient/{id}")
    public ResponseEntity<?> deletePatients(@PathVariable Long id)
    {
        try {
            Optional<Patient> patient = patientRepository.findById(id);
            if (patient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient is not found");
            }
            List<Appointment> allAppointments = appoinmentRepository.findByPatient(patient.get());
            for (Appointment oneAppointment : allAppointments) {
                appoinmentRepository.deleteById(oneAppointment.getId());
            }
            patientRepository.deleteById(id);
            return  ResponseEntity.ok("The patient is deleted");
        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteDoctor/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id)
    {
            try{
                Optional<Doctor> doctor = doctorRepository.findById(id);
                if(doctor.isEmpty())
                {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor is not found");
                }
                List<Appointment> appointments = appoinmentRepository.findByDoctor(doctor.get());
                for(Appointment oneAppointment:appointments)
                {
                    appoinmentRepository.deleteById(oneAppointment.getId());
                }
                doctorRepository.deleteById(id);
                return ResponseEntity.ok("The doctors is deleted");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
    }

    @DeleteMapping("/deleteHospital/{id}")
    public ResponseEntity<?> deleteHospital(@PathVariable Long id)
    {
            try{
                Optional<Hospital> matchedHospital = hospitalRepository.findById(id);
                if(matchedHospital.isEmpty())
                {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The hospital is not found");
                }
                List<Doctor> doctors = doctorRepository.findAll();
                List<Doctor> doctorList = new ArrayList<>();
                for(Doctor oneDoctor:doctors)
                {
                    if(oneDoctor.getHospital().getHospitalId()==matchedHospital.get().getHospitalId())
                    {
                        doctorList.add(oneDoctor);
                    }
                }
                for(Doctor doctor:doctorList)
                {
                    List<Appointment> appointmentList = appoinmentRepository.findByDoctor(doctor);
                    for(Appointment appointment:appointmentList)
                    {
                        appoinmentRepository.deleteById(appointment.getId());
                    }
                }
                hospitalRepository.deleteById(id);
                return ResponseEntity.ok("Hospital deleted");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
    }

    @DeleteMapping("/deleteHospitalStaff/{id}")
    public  ResponseEntity<?> deleteHospitalStaff(@PathVariable Long id)
    {
        try {
            hospitalStaffRepository.deleteById(id);
            return ResponseEntity.ok("Delete the hospital staff member");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteHealthCareManager/{id}")
    public ResponseEntity<?> deleteHealthCareManager(@PathVariable Long id)
    {
        try {
            hospitalStaffRepository.deleteById(id);
            return ResponseEntity.ok("Delete the hospital Health care manager");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getDashboardDetails")
    public ResponseEntity<?> getDashboardDetails()
    {
        long numberOfPatients = getTotalPatients.getNumberOfPatients();
        long numberOfDoctors = getTotalDoctors.getDoctorsCount();
        long totalAppointmentsCount = getTotalAppointments.getTotalAppointmentsCount();
        long totalBookedAppointmentsCount = getTotalAppointments.getTotalBookedAppointmentsCount();
        long totalCompletedAppointmentsCount = getTotalAppointments.getTotalCompletedAppointmentsCount();
        long totalAvailableAppointmentsCount = getTotalAppointments.getTotalAvailableAppointmentsCount();
        long totalHospitalStaffCount = getTotalHospitalStaffCount.getTotalHospitalStaffCount();
        long totalSecretaryCount = getTotalHospitalStaffCount.getTotalSecretaryCount();
        long totalHealthCareManagerCount = getTotalHospitalStaffCount.getTotalHealthCareManagerCount();
        Map<String,Long> response = new HashMap<>();
        response.put("NoOfPatient",numberOfPatients);
        response.put("NoOfDoctors",numberOfDoctors);
        response.put("TotalAppointmentsCount",totalAppointmentsCount);
        response.put("TotalBookedAppointments",totalBookedAppointmentsCount);
        response.put("TotalCompletedAppointments",totalCompletedAppointmentsCount);
        response.put("TotalAvailableAppointments",totalAvailableAppointmentsCount);
        response.put("TotalHospitalStaffCount",totalHospitalStaffCount);
        response.put("TotalSecretaryCount",totalSecretaryCount);
        response.put("TotalHealthCareManagerCount",totalHealthCareManagerCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getDepartmentVisePatients")
    public ResponseEntity<?> getDepartmentVisePatients()
    {
            return ResponseEntity.ok(weeklySpecializationService.getThisWeekSpecializationTotals());
    }

    @GetMapping("/getAppointmentsWithMonth")
    public ResponseEntity<?> getAppointmentsWithMonth()
    {
        return ResponseEntity.ok(getAppointmentsWithMonth.getNumberOfAppointmentsWithMonth());
    }

}
