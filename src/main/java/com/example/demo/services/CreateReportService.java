package com.example.demo.services;

import com.example.demo.dto.VisitReportResponse;
import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Status;
import com.example.demo.repository.AppoinmentRepository;
import com.example.demo.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreateReportService
{
    private final AppoinmentRepository appoinmentRepository;
    public CreateReportService(AppoinmentRepository appoinmentRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
    }

    @Transactional(readOnly = true,isolation = Isolation.REPEATABLE_READ)
    public VisitReportResponse createPatientReport(LocalDate fromDate,LocalDate toDate,
                                                   String hospital)
    {
        VisitReportResponse visitResponse = new VisitReportResponse();
        List<Appointment> allAppointments = appoinmentRepository.findAll();
        int visitAppointmentsCount = 0;
        Map<String,Integer> totalVisitsPerDepartment = new HashMap<>();
        Map<LocalDateTime,Integer> visitPerHour = new HashMap<>();
        for(Appointment appointment:allAppointments)
        {
            if(appointment.getStatus().equals(Status.COMPLETED))
            {
                if (appointment.getStartTime().toLocalDate().equals(fromDate)
                        || appointment.getStartTime().toLocalDate().equals(toDate)
                        || (appointment.getStartTime().toLocalDate().isAfter(fromDate)
                        && appointment.getStartTime().toLocalDate().isBefore(toDate))
                ) {
                    if (appointment.getDoctor().getHospital().getHospitalId().equals(Long.parseLong(hospital))) {
                        visitAppointmentsCount++;
                        String department = appointment.getDoctor().getSpecialization();
                        LocalDateTime appointmentStartTime = appointment.getStartTime().truncatedTo(ChronoUnit.HOURS);
                        visitPerHour.put(appointmentStartTime, visitPerHour.getOrDefault(appointmentStartTime, 0) + 1);
                        totalVisitsPerDepartment.put(department, totalVisitsPerDepartment.getOrDefault(department, 0) + 1);
                    }
                }
            }
        }
        visitResponse.setTotalVisits(visitAppointmentsCount);
        visitResponse.setTotalVisitPerDepartment(totalVisitsPerDepartment);
        long daysBetween = ChronoUnit.DAYS.between(fromDate,toDate)+1;
        double averagePerDay = (double)visitResponse.getTotalVisits()/daysBetween;
        visitResponse.setAveragePerDay(averagePerDay);
        LocalDateTime peakHour = visitPerHour.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        visitResponse.setPeakDayAndTime(peakHour);
        return visitResponse;
    }
}