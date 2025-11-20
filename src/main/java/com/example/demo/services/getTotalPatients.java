package com.example.demo.services;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class getTotalPatients {

    private final PatientRepository patientRepository;

    public getTotalPatients(PatientRepository patientRepository)
    {
            this.patientRepository = patientRepository;
    }

    public long getNumberOfPatients()
    {
        return patientRepository.count();
    }
}
