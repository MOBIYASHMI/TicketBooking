package com.example.application.restController;

import com.example.application.dto.BusDto;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusRestController {

    @Autowired
    private BusService busService;

    // Get all buses
    @GetMapping
    public ResponseEntity<List<BusDto>> getAllBuses() {
        List<BusDto> buses = busService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    // Get a bus by ID
    @GetMapping("/{busId}")
    public ResponseEntity<?> getBusById(@PathVariable Long busId) {
        try {
            BusDto bus = busService.getByBusId(busId);
            return ResponseEntity.ok(bus);
        } catch (BusNotFoundException e) {
            return ResponseEntity.status(404).body("Bus not found with ID: " + busId);
        }
    }

    // Get buses by bus type
    @GetMapping("/type")
    public ResponseEntity<List<BusDto>> getBusesByType(@RequestParam String busType) {
        List<BusDto> buses = busService.viewByBusType(busType);
        return ResponseEntity.ok(buses);
    }
}
