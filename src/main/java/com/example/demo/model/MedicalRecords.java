package com.example.demo.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "medicalrecords")
public class MedicalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordID;

    @ElementCollection
    @CollectionTable(
            name = "medicalrecord_allergies",
            joinColumns = @JoinColumn(name = "record_id")
    )
    @Column(name = "allergy")
    private List<String> allergies;

    @ElementCollection
    @CollectionTable(
            name = "medicalrecord_medications",
            joinColumns = @JoinColumn(name = "record_id")
    )
    @Column(name = "medication")
    private List<String> medications;

    public MedicalRecords()
    {}

    public MedicalRecords(List<String> allergies,List<String> medications)
    {
        this.allergies = allergies;
        this.medications = medications;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    public List<String> getAllergies()
    {
        return allergies;
    }

    public List<String> getMedications()
    {
        return medications;
    }

    public Long getRecordID()
    {
        return  recordID;
    }
}
