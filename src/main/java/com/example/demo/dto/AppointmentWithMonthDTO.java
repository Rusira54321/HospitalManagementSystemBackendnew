package com.example.demo.dto;

public class AppointmentWithMonthDTO {
    private String month;
    private long noOfAppointments;
    public AppointmentWithMonthDTO(String month,long noOfAppointments)
    {
        this.month = month;
        this.noOfAppointments = noOfAppointments;
    }

    public long getNoOfAppointments() {
        return noOfAppointments;
    }

    public String getMonth() {
        return month;
    }
}
