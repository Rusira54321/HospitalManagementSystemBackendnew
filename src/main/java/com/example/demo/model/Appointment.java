package com.example.demo.model;

import jakarta.persistence.*;

import javax.print.Doc;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(optional = true)
    private Patient patient;

    @ManyToOne(optional = false)
    private Doctor doctor;

    @Column(nullable = false)
    private String roomLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime bookedTime;

    private LocalDateTime completeTime;

    private Float price;

    public Appointment()
    {}
    public Appointment(LocalDateTime startTime,LocalDateTime endTime,Status status,
                       Float price,Doctor doctor,Patient patient,String roomLocation)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.price = price;
        this.doctor = doctor;
        this.patient = patient;
        this.roomLocation = roomLocation;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
    public void setCompleteTime(LocalDateTime completeTime)
    {
        this.completeTime = completeTime;
    }
    public LocalDateTime getCompleteTime()
    {
        return  completeTime;
    }
    public void setCreateTime(LocalDateTime createTime)
    {
        this.createTime = createTime;
    }
    public void setBookedTime(LocalDateTime bookedTime)
    {
        this.bookedTime = bookedTime;
    }
    public LocalDateTime getCreateTime()
    {
        return createTime;
    }
    public LocalDateTime getBookedTime()
    {
        return bookedTime;
    }
    public void setRoomLocation(String roomLocation)
    {
        this.roomLocation = roomLocation;
    }
    public String getRoomLocation()
    {
        return  roomLocation;
    }
    public Long getId()
    {
        return  id;
    }
    public void setPrice(Float price)
    {
        this.price = price;
    }
    public Float getPrice()
    {
        return price;
    }
    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
    }
    public void setPatient(Patient patient)
    {
        this.patient = patient;
    }
    public void setDoctor(Doctor doctor)
    {
        this.doctor = doctor;
    }
    public void setStatus(Status status)
    {
        this.status =status;
    }
    public LocalDateTime getStartTime()
    {
        return startTime;
    }
    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Status getStatus() {
        return status;
    }
}
