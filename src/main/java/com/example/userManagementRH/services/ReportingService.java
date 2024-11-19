package com.example.userManagementRH.services;

import com.example.userManagementRH.entities.Evaluation;
import com.example.userManagementRH.entities.TimeSheet;
import com.example.userManagementRH.repositories.EvaluationRepo;
import com.example.userManagementRH.repositories.HolidayRequestRepo;
import com.example.userManagementRH.repositories.TimeSheetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    @Autowired
    private HolidayRequestRepo holidayRequestRepo;

    @Autowired
    private EvaluationRepo evaluationRepo;

    @Autowired
    private TimeSheetRepo timeSheetRepo;

    public Map<String, Long> getLeaveCountPerEmployee() {
        return holidayRequestRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        leave -> leave.getUser().fullName(),
                        Collectors.counting()
                ));
    }

    public Map<String, Double> getAveragePerformanceScores() {
        return evaluationRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        eval -> eval.getUser().fullName(),
                        Collectors.averagingInt(Evaluation::getScore)
                ));
    }

    public Map<String, Integer> getTotalHoursWorked() {
        return timeSheetRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        ts -> ts.getUser().fullName(),
                        Collectors.summingInt(TimeSheet::getHoursWorked)
                ));
    }
    // fullName = firstName+lastName
}


