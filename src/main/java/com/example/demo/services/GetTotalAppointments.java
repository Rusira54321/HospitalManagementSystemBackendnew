package com.example.demo.services;

import com.example.demo.model.Status;
import com.example.demo.repository.AppoinmentRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTotalAppointments
{
    private final AppoinmentRepository appoinmentRepository;
    public GetTotalAppointments(AppoinmentRepository appoinmentRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
    }
    public long getTotalAppointmentsCount()
    {
        return appoinmentRepository.count();
    }
    public long getTotalCompletedAppointmentsCount()
    {
        return appoinmentRepository.countByStatus(Status.COMPLETED);
    }
    public long getTotalBookedAppointmentsCount()
    {
        return appoinmentRepository.countByStatus(Status.BOOKED);
    }
    public long getTotalAvailableAppointmentsCount()
    {
        return appoinmentRepository.countByStatus(Status.AVAILABLE);
    }
}
