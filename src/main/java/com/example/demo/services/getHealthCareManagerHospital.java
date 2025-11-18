package com.example.demo.services;

import com.example.demo.model.HospitalStaff;
import com.example.demo.repository.HospitalStaffRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class getHealthCareManagerHospital
{
    private final HospitalStaffRepository hospitalStaffRepository;
    public getHealthCareManagerHospital(HospitalStaffRepository hospitalStaffRepository)
    {
        this.hospitalStaffRepository = hospitalStaffRepository;
    }

    public Long getHospitalID(String healthCareManagerUserName)
    {
        Optional<HospitalStaff> healthCareManager = hospitalStaffRepository.findByUsername(healthCareManagerUserName);
        return healthCareManager.map(hospitalStaff -> hospitalStaff.hospital().getHospitalId()).orElse(null);
    }
}
