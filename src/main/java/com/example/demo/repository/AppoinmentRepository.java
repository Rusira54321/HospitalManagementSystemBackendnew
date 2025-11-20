package com.example.demo.repository;

import com.example.demo.model.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppoinmentRepository extends JpaRepository<Appointment,Long> {
        List<Appointment> findByDoctor(Doctor doctor);
        List<Appointment> findByPatient(Patient patient);
        long countByStatus(Status status);


        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor")
        List<Appointment> findByDoctorWithLock(@Param("doctor") Doctor doctor);

        @Query("SELECT d.specialization AS specialization, COUNT(a) AS total " +
                "FROM Appointment a " +
                "JOIN a.doctor d " +
                "WHERE a.startTime BETWEEN :start AND :end " +
                "AND a.status IN ('BOOKED', 'COMPLETED') " +
                "GROUP BY d.specialization")
        List<Object[]> getWeeklyAppointmentsBySpecialization(
                @Param("start") LocalDateTime start,
                @Param("end") LocalDateTime end
        );

        @Query(value="SELECT MONTH(start_time) AS month, COUNT(*) AS total " +
                "FROM appointments " +
                "WHERE status IN ('BOOKED', 'COMPLETED') " +
                "AND YEAR(start_time) = YEAR(CURDATE()) " +
                "GROUP BY MONTH(start_time) " +
                "ORDER BY MONTH(start_time)",
                nativeQuery = true)
        List<Object[]> getCurrentYearAppointmentsByMonth();


}