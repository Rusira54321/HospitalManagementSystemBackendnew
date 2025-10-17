package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "patients")
public class Patient extends User {

    @JsonProperty("FirstName")
    @Column(nullable = false)
    private String FirstName;

    @JsonProperty("LastName")
    @Column(nullable = false)
    private String LastName;

    @JsonProperty("dateOfBirth")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @JsonProperty("gender")
    @Column(nullable = false)
    private String gender;

    @JsonProperty("PhoneNumber")
    @Column
    private String PhoneNumber;

    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "medicalrecords_id")
    private MedicalRecords medicalRecords;

    public Patient()
    {

    }

    public Patient(String FirstName, String LastName,LocalDate dateOfBirth,String gender, String PhoneNumber, String username, String password, String email)
    {
        super(username,password,email);
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.gender = gender;
        this.PhoneNumber = PhoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public void setMedicalRecords(MedicalRecords medicalRecords)
    {
        this.medicalRecords = medicalRecords;
    }
    public MedicalRecords getMedicalRecords()
    {
        return  medicalRecords;
    }
    public void setPhoneNumber(String PhoneNumber)
    {
        this.PhoneNumber = PhoneNumber;
    }

    public  void setFirstName(String FirstName)
    {
        this.FirstName = FirstName;
    }

    public void setLastName(String LastName)
    {
        this.LastName = LastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }
    public String getFirstName()
    {
        return FirstName;
    }
    public String getLastName()
    {
        return LastName;
    }
    public LocalDate getDateOfBirth()
    {
        return dateOfBirth;
    }
    public String getGender()
    {
        return gender;
    }
    public String getPhoneNumber()
    {
        return PhoneNumber;
    }
}
