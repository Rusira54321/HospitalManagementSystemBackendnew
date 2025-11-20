package com.example.demo.repository;

import com.example.demo.model.Hospital;
import com.example.demo.model.HospitalStaff;
import com.example.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalStaffRepository extends JpaRepository<HospitalStaff,Long>
{
    List<HospitalStaff> findByHospital(Hospital hospital);
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);
    Optional<HospitalStaff> findByUsername(String username);
}