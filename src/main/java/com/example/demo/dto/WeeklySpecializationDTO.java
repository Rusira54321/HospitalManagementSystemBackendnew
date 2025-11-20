package com.example.demo.dto;

public class WeeklySpecializationDTO
{
    private String specialization;
    private Long total;
    public WeeklySpecializationDTO(String specialization,Long total)
    {
        this.specialization = specialization;
        this.total = total;
    }
    public String getSpecialization()
    {
        return specialization;
    }

    public Long getTotal()
    {
        return total;
    }
}
