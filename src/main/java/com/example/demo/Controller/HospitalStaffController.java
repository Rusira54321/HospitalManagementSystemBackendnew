package com.example.demo.Controller;

import com.example.demo.model.*;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.HospitalStaffRepository;
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
    public HospitalStaffController(DoctorRepository doctorRepository,
                                   HospitalRepository hospitalRepository,
                    HospitalStaffRepository hospitalStaffRepository)
    {
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.hospitalStaffRepository = hospitalStaffRepository;
    }

    @GetMapping("/getDoctors")
    public ResponseEntity<?> getDoctorsRelatedToSecretary(@RequestParam String SecretaryUserName)
    {
        if(SecretaryUserName.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Secretary User name" +
                    "is missing in the request");
        }
        try {
            Optional<HospitalStaff> hospitalStaff = hospitalStaffRepository.findByUsername(SecretaryUserName);
            if (hospitalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Secretary is not found");
            }
            HospitalStaff hospitalStaffObject = hospitalStaff.get();
            List<Doctor> doctors = doctorRepository.findByHospitalStaff(hospitalStaffObject);
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server error");
        }
    }

}
