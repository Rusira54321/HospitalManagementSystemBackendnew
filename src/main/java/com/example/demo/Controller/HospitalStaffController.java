package com.example.demo.Controller;

import com.example.demo.model.Doctor;
import com.example.demo.model.Hospital;
import com.example.demo.model.HospitalStaff;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.HospitalStaffRepository;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.Doc;
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


}
