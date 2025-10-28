package com.example.demo.repository;

import com.example.demo.model.Hospital;
import com.example.demo.model.HospitalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalStaffRepository extends JpaRepository<HospitalStaff,Long>
{
    List<HospitalStaff> findByHospital(Hospital hospital);
}