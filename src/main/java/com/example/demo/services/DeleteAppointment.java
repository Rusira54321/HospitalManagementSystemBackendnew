package com.example.demo.services;

import com.example.demo.repository.AppoinmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAppointment
{
    private final AppoinmentRepository appoinmentRepository;

    public DeleteAppointment(AppoinmentRepository appoinmentRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
    }

    public void deleteAppointment(Long appointmentId)
    {
        appoinmentRepository.deleteById(appointmentId);
    }
}
