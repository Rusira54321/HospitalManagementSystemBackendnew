package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.HospitalStaffRepository;
import com.example.demo.services.DeleteAppointment;
import com.example.demo.services.GetDoctorsofSecretaries;
import com.example.demo.services.getHospitalStaffByUserName;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/HospitalStaff")
public class HospitalStaffController
{
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalStaffRepository hospitalStaffRepository;
    private final getHospitalStaffByUserName getHospitalStaffByUserName;
    private final GetDoctorsofSecretaries getDoctorsofSecretaries;
    private final DeleteAppointment deleteAppointment;
    public HospitalStaffController(DoctorRepository doctorRepository,
                                   HospitalRepository hospitalRepository,
                    HospitalStaffRepository hospitalStaffRepository,
                                   getHospitalStaffByUserName getHospitalStaffByUserName,
                                   GetDoctorsofSecretaries getDoctorsofSecretaries,
                                   DeleteAppointment deleteAppointment
                                   )
    {
        this.deleteAppointment = deleteAppointment;
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.hospitalStaffRepository = hospitalStaffRepository;
        this.getHospitalStaffByUserName = getHospitalStaffByUserName;
        this.getDoctorsofSecretaries = getDoctorsofSecretaries;
    }



    @GetMapping("/getDoctors")
    public ResponseEntity<?> getDoctors(@RequestParam String hospitalStaffUserName)
    {
        if(hospitalStaffUserName==null || hospitalStaffUserName.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The hospital staff user name " +
                    "is not include in the request");
        }
        HospitalStaff matchedHospitalStaffMember = getHospitalStaffByUserName.getHospitalStaff(hospitalStaffUserName);
        if(matchedHospitalStaffMember==null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hospital staff member us not found");
        }
        List<Doctor> doctors = getDoctorsofSecretaries.getDoctorsRelatedToSecretary(matchedHospitalStaffMember);
        if(doctors.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("doctors are not found");
        }
        return ResponseEntity.ok(doctors);
    }

    @DeleteMapping("/deleteAppointment")
    public ResponseEntity<?> deleteAppointment(@RequestParam  Long appointmentId)
    {
        if(appointmentId==null)
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The appointmentId is null value");
        }
        deleteAppointment.deleteAppointment(appointmentId);
        return ResponseEntity.ok("Successfully deleted");
    }
}
