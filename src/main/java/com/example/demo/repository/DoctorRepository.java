package com.example.demo.repository;

import com.example.demo.model.Doctor;
import com.example.demo.model.HospitalStaff;
import jakarta.persistence.LockModeType;
import org.hibernate.LockMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Optional<Doctor> findByUsername(String username);
    List<Doctor> findByHospitalStaff(HospitalStaff hospitalStaff);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Doctor d WHERE d.id = :id")
    Doctor findByIdWithLock(@Param("id") Long id);
}
