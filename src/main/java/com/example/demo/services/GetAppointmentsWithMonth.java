package com.example.demo.services;

import com.example.demo.dto.AppointmentWithMonthDTO;
import com.example.demo.repository.AppoinmentRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetAppointmentsWithMonth
{
    private final AppoinmentRepository appoinmentRepository;
    public GetAppointmentsWithMonth(AppoinmentRepository appoinmentRepository)
    {
        this.appoinmentRepository = appoinmentRepository;
    }
    public List<AppointmentWithMonthDTO> getNumberOfAppointmentsWithMonth()
    {
        int currentYear = Year.now().getValue();
        List<Object[]> numberOfAppointmentsWithMonth = appoinmentRepository.getCurrentYearAppointmentsByMonth();
        List<AppointmentWithMonthDTO> NumberOfappointments = new ArrayList<>();
        for(Object[] appointment:numberOfAppointmentsWithMonth)
        {
            AppointmentWithMonthDTO newObject = new AppointmentWithMonthDTO((String)appointment[1],(Long)appointment[2]);
            NumberOfappointments.add(newObject);
        }
        return NumberOfappointments;
    }
}
