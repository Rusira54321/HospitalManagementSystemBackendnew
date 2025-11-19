package com.example.demo.services;

import com.example.demo.model.HospitalStaff;
import com.example.demo.repository.HospitalStaffRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class getHospitalStaffByUserName {
    private final HospitalStaffRepository hospitalStaffRepository;
    public getHospitalStaffByUserName(HospitalStaffRepository hospitalStaffRepository)
    {
        this.hospitalStaffRepository = hospitalStaffRepository;
    }
    public HospitalStaff getHospitalStaff(String userName)
    {
        Optional<HospitalStaff> matchedHospitalStaff = hospitalStaffRepository.findByUsername(userName);
        return matchedHospitalStaff.orElse(null);
    }
}
