package com.example.demo.services;

import com.example.demo.repository.HospitalStaffRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTotalHospitalStaffCount
{
    private final HospitalStaffRepository hospitalStaffRepository;
    public GetTotalHospitalStaffCount(HospitalStaffRepository hospitalStaffRepository)
    {
        this.hospitalStaffRepository = hospitalStaffRepository;
    }
    public long getTotalHospitalStaffCount()
    {
        return hospitalStaffRepository.count();
    }
    public long getTotalSecretaryCount()
    {
        return hospitalStaffRepository.countByRoleName("ROLE_HOSPITALSTAFF");
    }
    public long getTotalHealthCareManagerCount()
    {
        return  hospitalStaffRepository.countByRoleName("ROLE_HEALTHCAREMANAGER");
    }
}
