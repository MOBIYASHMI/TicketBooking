package com.example.application.controller;

import com.example.application.dto.BusDto;
import com.example.application.dto.ScheduleDto;
import com.example.application.exceptions.BusAlreadyExistsException;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.service.BusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BusService busService;


    @GetMapping("/login")
    public String showLoginForm(){
        return "adminlogin";
    }

    @GetMapping("/dashboard")
    public String getAllBuses(Model model){
        List<BusDto> buses=busService.getAllBuses();
        model.addAttribute("buses",buses);
        return "admindashboard";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddBusForm(Model model){
        BusDto busDto=new BusDto();
        busDto.setSchedules(new ArrayList<>());
        busDto.getSchedules().add(new ScheduleDto());
        model.addAttribute("busDto",busDto);
        return "addbus";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addBusAndSchedules(@Valid @ModelAttribute("busDto") BusDto busDto, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            model.addAttribute("error",result.hasErrors());
            return "addbus";
        }
        try{
            busService.createBusWithSchedules(busDto);
            redirectAttributes.addFlashAttribute("message","Bus and schedules added successfully!");
            return "redirect:/admin/dashboard";
        }catch (BusAlreadyExistsException ex){
            result.rejectValue("busName","error.bus", ex.getMessage());
            return "addbus";
        }
    }

    @GetMapping("/schedules/editExisting/{busId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showExistingSchedulesForm(@PathVariable Long busId, Model model) {
        try {
            BusDto busDto = busService.getByBusId(busId);
            model.addAttribute("busDto", busDto);
            return "editExistingSchedules";
        } catch (BusNotFoundException ex) {
            model.addAttribute("errorMessage", "Bus not found with ID: " + busId);
            return "error"; // Or redirect to an error page
        }
    }

    @PostMapping("/schedules/updateExisting")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateExistingBusSchedules(@Valid @ModelAttribute("busDto") BusDto busDto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "editExistingSchedules";
        }
        try {
            busService.updateExistingSchedules(busDto); // New service method
            redirectAttributes.addFlashAttribute("message", "Bus schedules updated successfully!");
            return "redirect:/admin/dashboard";
        } catch (BusNotFoundException ex) {
            result.rejectValue("id", "error.bus", ex.getMessage());
            return "editExistingSchedules";
        }
    }

    @GetMapping("/schedules/add/{busId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddSchedulesForm(@PathVariable Long busId, Model model) {
        try {
            BusDto busDto = busService.getByBusId(busId);
            if (busDto.getSchedules() == null || busDto.getSchedules().isEmpty()) {
                busDto.setSchedules(new ArrayList<>());
                busDto.getSchedules().add(new ScheduleDto());
            }
            model.addAttribute("busDto", busDto);
            return "addSchedules";
        } catch (BusNotFoundException ex) {
            model.addAttribute("errorMessage", "Bus not found with ID: " + busId);
            return "error";
        }
    }

    @PostMapping("/schedules/addNew")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveBusSchedules(@Valid @ModelAttribute("busDto") BusDto busDto, BindingResult result, RedirectAttributes redirectAttributes,Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error",result.hasErrors());
            return "addSchedules";
        }
        try {
            busService.addNewSchedules(busDto);
            redirectAttributes.addFlashAttribute("message", "Bus schedules updated successfully!");
            return "redirect:/admin/dashboard";
        } catch (BusNotFoundException ex) {
            result.rejectValue("id", "error.bus", ex.getMessage());
            return "addSchedules";
        }
    }

    @GetMapping("/schedules/view/{busId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewSchedules(@PathVariable Long busId, Model model) {
        try {
            BusDto busDto = busService.getByBusId(busId);
            model.addAttribute("busDto", busDto);
            model.addAttribute("schedules", busDto.getSchedules());
            return "viewSchedules";
        } catch (BusNotFoundException ex) {
            model.addAttribute("errorMessage", "Bus not found with ID: " + busId);
            return "error"; // Or redirect to an error page
        }
    }

    @GetMapping("/buses/delete/{busId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBus(@PathVariable Long busId, RedirectAttributes redirectAttributes) {
        try {
            busService.deleteBusAndSchedules(busId); // New service method
            redirectAttributes.addFlashAttribute("message", "Bus and all its schedules deleted successfully!");
        } catch (BusNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bus not found with ID: " + busId);
        }
        return "redirect:/admin/dashboard";
    }
}
