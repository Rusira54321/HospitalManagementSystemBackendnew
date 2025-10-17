package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "hospitalStaff")
public class HospitalStaff extends  User{


    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hospital_id",nullable = false)
    @JsonBackReference
    private Hospital hospital;

    public HospitalStaff()
    {}

    public HospitalStaff(String firstName, String lastName, String username, String password,String email)
    {
        super(username,password,email);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setHospital(Hospital hospital)
    {
        this.hospital = hospital;
    }
    public Hospital hospital()
    {
        return hospital;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }



    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }


}
