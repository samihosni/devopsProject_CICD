package com.example.userManagementRH.services;

import com.example.userManagementRH.entities.TimeSheet;
import com.example.userManagementRH.repositories.TimeSheetRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TimeSheetTestService {

    @InjectMocks
    private TimeSheetService timeSheetService;

    @Mock
    private TimeSheetRepo timeSheetRepo;

    private TimeSheet mockTimeSheet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockTimeSheet = new TimeSheet(1L, null, LocalDate.now(), 8, false); // Use a mock User object here if needed
    }

    @Test
    void createTimesheet_ShouldSaveAndReturnTimeSheet() {
        // Arrange: Mock save behavior
        when(timeSheetRepo.save(any(TimeSheet.class))).thenReturn(mockTimeSheet);

        // Act: Call the service method
        TimeSheet savedTimeSheet = timeSheetService.createTimesheet(mockTimeSheet);

        // Assert: Check if savedTimeSheet is not null and contains correct values
        assertNotNull(savedTimeSheet);
        assertEquals(8, savedTimeSheet.getHoursWorked());
        assertFalse(savedTimeSheet.getValidated()); // Since initial validated is false
        verify(timeSheetRepo, times(1)).save(mockTimeSheet);
    }

    @Test
    void getAllTimesheets_ShouldReturnListOfTimesheets() {
        // Arrange: Mock findAll behavior
        when(timeSheetRepo.findAll()).thenReturn(List.of(mockTimeSheet));

        // Act: Call the service method
        List<TimeSheet> allTimeSheets = timeSheetService.getAllTimesheets();

        // Assert: Check if the returned list is not empty
        assertNotNull(allTimeSheets);
        assertFalse(allTimeSheets.isEmpty());
        assertEquals(1, allTimeSheets.size()); // Since we mocked a single timesheet
    }

    @Test
    void updateTimesheet_ShouldUpdateAndReturnTimeSheet() {
        // Arrange: Mock findById and save
        when(timeSheetRepo.findById(1L)).thenReturn(Optional.of(mockTimeSheet));
        when(timeSheetRepo.save(any(TimeSheet.class))).thenReturn(mockTimeSheet);

        // Act: Update the time sheet's hoursWorked
        mockTimeSheet.setHoursWorked(10);
        TimeSheet updatedTimeSheet = timeSheetService.updateTimesheet(1L, mockTimeSheet);

        // Assert: Check if the time sheet was updated correctly
        assertEquals(10, updatedTimeSheet.getHoursWorked());
        verify(timeSheetRepo, times(1)).findById(1L);
        verify(timeSheetRepo, times(1)).save(mockTimeSheet);
    }

    @Test
    void validateTimesheet_ShouldMarkAsValidated() {
        // Arrange: Mock findById and save
        when(timeSheetRepo.findById(1L)).thenReturn(Optional.of(mockTimeSheet));
        when(timeSheetRepo.save(any(TimeSheet.class))).thenReturn(mockTimeSheet);

        // Act: Call the service method to validate the time sheet
        TimeSheet validatedTimeSheet = timeSheetService.validateTimesheet(1L);

        // Assert: Check if the validated field is set to true
        assertTrue(validatedTimeSheet.getValidated());
        verify(timeSheetRepo, times(1)).findById(1L);
        verify(timeSheetRepo, times(1)).save(mockTimeSheet);
    }

    @Test
    void deleteTimesheet_ShouldCallDeleteMethod() {
        // Arrange: Mock the deleteById behavior
        doNothing().when(timeSheetRepo).deleteById(1L);

        // Act: Call the delete method
        timeSheetService.deleteTimesheet(1L);

        // Assert: Check if deleteById was called once
        verify(timeSheetRepo, times(1)).deleteById(1L);
    }

    @Test
    void updateTimesheet_ShouldThrowException_WhenNotFound() {
        // Arrange: Mock findById to return an empty Optional
        when(timeSheetRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert: Expect exception when updating a non-existent time sheet
        assertThrows(RuntimeException.class, () -> {
            timeSheetService.updateTimesheet(1L, mockTimeSheet);
        });
    }
}
