package com.example.demo.services;

import com.example.demo.model.Doctor;
import com.example.demo.model.HospitalStaff;
import com.example.demo.repository.DoctorRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class GetDoctorsofSecretaries {
    private final DoctorRepository doctorRepository;
    public GetDoctorsofSecretaries(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }
    public List<Doctor> getDoctorsRelatedToSecretary(HospitalStaff hospitalStaff)
    {
        return doctorRepository.findByHospitalStaff(hospitalStaff);
    }
}
