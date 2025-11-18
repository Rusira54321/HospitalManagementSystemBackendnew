package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class VisitReportResponse
{
    private int totalVisits;
    private Map<String,Integer> totalVisitPerDepartment;
    private double averagePerDay;
    private LocalDateTime peakDayAndTime;

    public VisitReportResponse()
    {}

    public void setTotalVisits(int totalVisits)
    {
        this.totalVisits = totalVisits;
    }

    public void setTotalVisitPerDepartment(Map<String,Integer> totalVisitPerDepartment)
    {
        this.totalVisitPerDepartment = totalVisitPerDepartment;
    }

    public void setAveragePerDay(double averagePerDay)
    {
        this.averagePerDay = averagePerDay;
    }

    public void setPeakDayAndTime(LocalDateTime peakDayAndTime)
    {
        this.peakDayAndTime = peakDayAndTime;
    }

    public int getTotalVisits()
    {
        return totalVisits;
    }

    public Map<String,Integer> getTotalVisitPerDepartment()
    {
        return totalVisitPerDepartment;
    }

    public double getAveragePerDay()
    {
        return averagePerDay;
    }

    public LocalDateTime getPeakDayAndTime()
    {
        return peakDayAndTime;
    }
}
