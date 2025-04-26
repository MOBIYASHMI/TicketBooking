package com.example.application.controller;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.service.BusService;
import com.example.application.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/buses")
public class BusController {

    @Autowired
    private BusService busService;

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/viewAllBuses")
    public String getAllBuses(Model model){
        List<BusDto> buses=busService.getAllBuses();
        model.addAttribute("buses",buses);
//        List<ScheduleDto> schedules=scheduleService.getAllSchedules();
//        model.addAttribute("schedules",schedules);
        return "busList";
    }

    @GetMapping("/{busId}")
    public String getBusById(@PathVariable Long busId, Model model){
        try {
            BusDto bus = busService.getByBusId(busId);
            model.addAttribute("bus", bus);
            return "seatBooking";
        } catch (BusNotFoundException e) {
            model.addAttribute("error", "Bus not found with ID: " + busId);
            return "busList";
        }
    }

    @GetMapping("/viewBuses")
    public String viewBusByType(@RequestParam String busType,Model model){
        List<BusDto> buses=busService.viewByBusType(busType);
        model.addAttribute("buses",buses);
        model.addAttribute("busType",busType);
        return "busList";
    }


}
