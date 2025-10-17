package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hospitals")
public class Hospital
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalId;

    @Column(nullable = false)
    private String hospitalName;

    @Column(nullable = false)
    private String hospitalLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HospitalType hospitalType;

    @OneToMany(mappedBy = "hospital" ,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "hospital",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<HospitalStaff> hospitalStaffs;

    public Hospital()
    {}

    public Hospital(String hospitalName,String hospitalLocation,HospitalType hospitalType)
    {
        this.hospitalLocation = hospitalLocation;
        this.hospitalName = hospitalName;
        this.hospitalType = hospitalType;
    }

    public void setHospitalId(Long hospitalId)
    {
        this.hospitalId = hospitalId;
    }
    public void setHospitalType(HospitalType hospitalType) { this.hospitalType = hospitalType;}
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setHospitalLocation(String hospitalLocation) {
        this.hospitalLocation = hospitalLocation;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public void setHospitalStaffs(List<HospitalStaff> hospitalStaffs)
    {
        this.hospitalStaffs = hospitalStaffs;
    }
    public Long getHospitalId()
    {
        return hospitalId;
    }

    public String getHospitalName()
    {
        return  hospitalName;
    }

    public String getHospitalLocation()
    {
        return  hospitalLocation;
    }

    public List<Doctor> getDoctors()
    {
        return  doctors;
    }
    public List<HospitalStaff> getHospitalStaffs()
    {
        return hospitalStaffs;
    }
    public HospitalType getHospitalType(){return hospitalType;}
}
