package com.example.demo.services;

import com.example.demo.dto.WeeklySpecializationDTO;
import com.example.demo.repository.AppoinmentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeeklySpecializationService
{
    private final AppoinmentRepository appoinmentRepository;

    public WeeklySpecializationService(AppoinmentRepository appoinmentRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
    }

    public List<WeeklySpecializationDTO> getThisWeekSpecializationTotals()
    {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = endOfWeek.atTime(23, 59, 59);
        List<Object[]> raw =
                appoinmentRepository.getWeeklyAppointmentsBySpecialization(start, end);
        List<WeeklySpecializationDTO> result = new ArrayList<>();

        for (Object[] row : raw) {
            result.add(new WeeklySpecializationDTO(
                    (String) row[0],      // specialization
                    (Long) row[1]         // total appointments
            ));
        }

        return result;
    }
}
