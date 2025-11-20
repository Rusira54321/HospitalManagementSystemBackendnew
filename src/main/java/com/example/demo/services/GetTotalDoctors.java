package com.example.demo.services;

import com.example.demo.repository.DoctorRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTotalDoctors
{
    private final DoctorRepository doctorRepository;
    public GetTotalDoctors(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }
    public long getDoctorsCount()
    {
        return doctorRepository.count();
    }
}
