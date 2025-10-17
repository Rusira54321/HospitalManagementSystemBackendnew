package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicalrecords")
public class MedicalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordID;

    @Column
    private String allergies;

    @Column
    private String medication;

    @Column
    private String visitHistory;


    public MedicalRecords()
    {}

    public MedicalRecords(String allergies,String medication,String visitHistory)
    {
        this.allergies = allergies;
        this.medication = medication;
        this.visitHistory = visitHistory;
    }


    public void setAllergies(String allergies)
    {
        this.allergies = allergies;
    }

    public void setMedication(String medication)
    {
        this.medication = medication;
    }

    public void setVisitHistory(String visitHistory)
    {
        this.visitHistory = visitHistory;
    }

    public Long getRecordID()
    {
        return recordID;
    }

    public String getAllergies()
    {
        return  allergies;
    }

    public String getMedication()
    {
        return  medication;
    }

    public String getVisitHistory()
    {
        return visitHistory;
    }
}
