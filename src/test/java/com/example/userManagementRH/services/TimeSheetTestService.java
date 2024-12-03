package com.example.userManagementRH.services;

import com.example.userManagementRH.entities.TimeSheet;
import com.example.userManagementRH.repositories.TimeSheetRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
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
        mockTimeSheet = new TimeSheet(1L, null, LocalDate.now(), 8, false);
    }

    @Test
    void createTimesheet_ShouldSaveAndReturnTimeSheet() {
        when(timeSheetRepo.save(any(TimeSheet.class))).thenReturn(mockTimeSheet);

        TimeSheet savedTimeSheet = timeSheetService.createTimesheet(mockTimeSheet);

        assertNotNull(savedTimeSheet);
        assertEquals(8, savedTimeSheet.getHoursWorked());
        verify(timeSheetRepo, times(1)).save(mockTimeSheet);
    }

    @Test
    void updateTimesheet_ShouldUpdateAndReturnTimeSheet() {
        when(timeSheetRepo.findById(1L)).thenReturn(Optional.of(mockTimeSheet));
        when(timeSheetRepo.save(any(TimeSheet.class))).thenReturn(mockTimeSheet);

        mockTimeSheet.setHoursWorked(10);
        TimeSheet updatedTimeSheet = timeSheetService.updateTimesheet(1L, mockTimeSheet);

        assertEquals(10, updatedTimeSheet.getHoursWorked());
        verify(timeSheetRepo, times(1)).findById(1L);
        verify(timeSheetRepo, times(1)).save(mockTimeSheet);
    }

    @Test
    void validateTimesheet_ShouldMarkAsValidated() {
        when(timeSheetRepo.findById(1L)).thenReturn(Optional.of(mockTimeSheet));
        when(timeSheetRepo.save(any(TimeSheet.class))).thenReturn(mockTimeSheet);

        TimeSheet validatedTimeSheet = timeSheetService.validateTimesheet(1L);

        assertTrue(validatedTimeSheet.getValidated());
        verify(timeSheetRepo, times(1)).findById(1L);
        verify(timeSheetRepo, times(1)).save(mockTimeSheet);
    }

    @Test
    void deleteTimesheet_ShouldCallDeleteMethod() {
        doNothing().when(timeSheetRepo).deleteById(1L);

        timeSheetService.deleteTimesheet(1L);

        verify(timeSheetRepo, times(1)).deleteById(1L);
    }
}
