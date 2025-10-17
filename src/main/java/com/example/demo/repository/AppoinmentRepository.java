package com.example.demo.repository;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Hospital;
import com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppoinmentRepository extends JpaRepository<Appointment,Long> {
        List<Appointment> findByDoctor(Doctor doctor);
        List<Appointment> findByPatient(Patient patient);
}