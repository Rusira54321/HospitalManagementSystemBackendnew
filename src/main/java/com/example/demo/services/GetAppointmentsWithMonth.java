package com.example.demo.services;

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
    public Map<String,Long> getNumberOfAppointmentsWithMonth()
    {
        int currentYear = Year.now().getValue();
        List<Object[]> numberOfAppointmentsWithMonth = appoinmentRepository.getCurrentYearAppointmentsByMonth();
        Map<String,Long> AppointmentsByMonth = new HashMap<>();
        for(Object[] appointment:numberOfAppointmentsWithMonth)
        {
            AppointmentsByMonth.put((String)appointment[0],(Long) appointment[1]);
        }
        return AppointmentsByMonth;
    }
}
