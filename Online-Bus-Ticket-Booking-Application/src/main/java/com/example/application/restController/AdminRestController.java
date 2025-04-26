package com.example.application.restController;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.exceptions.BusAlreadyExistsException;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.service.BusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {

    @Autowired
    private BusService busService;

    @GetMapping("/buses")
    public ResponseEntity<List<BusDto>> getAllBuses() {
        List<BusDto> buses=busService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    @PostMapping("/buses")
    public String addBusAndSchedules(@Valid @RequestBody BusDto busDto) {
        try {
            busService.createBusWithSchedules(busDto);
            return "Bus and schedules added successfully!";
        } catch (BusAlreadyExistsException ex) {
            return ex.getMessage();
        }
    }

    @GetMapping("/buses/{busId}")
    public BusDto getBusById(@PathVariable Long busId) {
        return busService.getByBusId(busId);
    }

    @PutMapping("/schedules/update")
    public String updateExistingBusSchedules(@Valid @RequestBody BusDto busDto) {
        try {
            busService.updateExistingSchedules(busDto);
            return "Bus schedules updated successfully!";
        } catch (BusNotFoundException ex) {
            return ex.getMessage();
        }
    }

    @PostMapping("/schedules/add")
    public String addNewSchedules(@Valid @RequestBody BusDto busDto) {
        try {
            busService.addNewSchedules(busDto);
            return "Bus schedules added successfully!";
        } catch (BusNotFoundException ex) {
            return ex.getMessage();
        }
    }

    @GetMapping("/schedules/view/{busId}")
    public List<ScheduleDto> viewSchedules(@PathVariable Long busId) {
        BusDto busDto = busService.getByBusId(busId);
        return busDto.getSchedules();
    }

    @DeleteMapping("/buses/{busId}")
    public String deleteBus(@PathVariable Long busId) {
        try {
            busService.deleteBusAndSchedules(busId);
            return "Bus and all its schedules deleted successfully!";
        } catch (BusNotFoundException ex) {
            return ex.getMessage();
        }
    }
}
