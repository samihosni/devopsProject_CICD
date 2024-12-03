package com.example.userManagementRH.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.userManagementRH.entities.TimeSheet;
import com.example.userManagementRH.repositories.TimeSheetRepo;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TimeSheetTestRestController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TimeSheetRepo timeSheetRepo;

    @Test
    void createTimesheet_ShouldReturnCreatedTimeSheet() throws Exception {
        String json = """
                {
                    "hoursWorked": 8,
                    "date": "2024-12-03",
                    "validated": false
                }
                """;

        mockMvc.perform(post("/timeSheet/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hoursWorked", is(8)));
    }

    @Test
    void getAllTimesheets_ShouldReturnListOfTimesheets() throws Exception {
        timeSheetRepo.save(new TimeSheet(null, null, LocalDate.now(), 8, false));

        mockMvc.perform(get("/timeSheet/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void updateTimesheet_ShouldUpdateAndReturnTimesheet() throws Exception {
        TimeSheet savedTimeSheet = timeSheetRepo.save(new TimeSheet(null, null, LocalDate.now(), 8, false));
        String json = """
                {
                    "hoursWorked": 10,
                    "date": "2024-12-03",
                    "validated": true
                }
                """;

        mockMvc.perform(put("/timeSheet/update/" + savedTimeSheet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hoursWorked", is(10)));
    }
}
