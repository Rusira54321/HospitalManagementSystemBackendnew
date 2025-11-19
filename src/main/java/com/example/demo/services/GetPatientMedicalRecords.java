package com.example.demo.services;

import com.example.demo.model.MedicalRecords;
import com.example.demo.model.Patient;
import com.example.demo.repository.MedicalRecordsRepository;
import com.example.demo.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPatientMedicalRecords {
    private final PatientRepository patientRepository;
    public GetPatientMedicalRecords(MedicalRecordsRepository medicalRecordsRepository,
                                    PatientRepository patientRepository)
    {
        this.patientRepository = patientRepository;
    }
    public MedicalRecords getMedicalRecords(String patientUserName)
    {
        Optional<Patient> matchedPatient = patientRepository.findByUsername(patientUserName);
        if(matchedPatient.isEmpty())
        {
            return null;
        }
        MedicalRecords medicalRecord = matchedPatient.get().getMedicalRecords();
        return medicalRecord;
    }
}
