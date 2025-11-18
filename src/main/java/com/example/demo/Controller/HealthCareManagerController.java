package com.example.demo.Controller;

import com.example.demo.dto.VisitReportResponse;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.repository.HospitalRepository;
import com.example.demo.repository.HospitalStaffRepository;
import com.example.demo.services.CreateReportService;
import com.example.demo.services.getHealthCareManagerHospital;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/HealthCareManager")
public class HealthCareManagerController
{

    private final CreateReportService createReportService;
    private final getHealthCareManagerHospital getHealthCareManagerHospital;
    public HealthCareManagerController(CreateReportService createReportService,
                                       getHealthCareManagerHospital getHealthCareManagerHospital)
    {
        this.createReportService = createReportService;
        this.getHealthCareManagerHospital = getHealthCareManagerHospital;
    }

    @GetMapping("/getPatientVisitReport")
    public  ResponseEntity<?> getPatientVisitReport(@RequestParam LocalDate fromDate,
                                                    @RequestParam LocalDate toDate,
                                                    @RequestParam String hospital)
    {

        // Hospital validation
        if(hospital==null || hospital.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The all fields are required");
        }
        //date range validation
        if(fromDate.isAfter(toDate))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date range: fromDate" +
                    " cannot be after toDate");
        }
        if(toDate.isAfter(LocalDate.now()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ToDate cannot be in the future");
        }
        try {
            VisitReportResponse visitReportResponse = createReportService.createPatientReport(fromDate, toDate, hospital);
            return ResponseEntity.ok(visitReportResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getHospital")
    public ResponseEntity<?>  getHealthCareManagerHospital(@RequestParam String healthCareManagerUserName)
    {
        if(healthCareManagerUserName==null || healthCareManagerUserName.isEmpty())
        {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request is missing healthCareManagerId");
        }
        try {
            Long hospitalId = getHealthCareManagerHospital.getHospitalID(healthCareManagerUserName);
            if(hospitalId==null)
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The hospital is not found");
            }
            return ResponseEntity.ok(hospitalId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
