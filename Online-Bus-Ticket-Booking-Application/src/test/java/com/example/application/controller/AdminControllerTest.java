package com.example.application.controller;

import com.example.application.dto.BusDto;
import com.example.application.exceptions.BusAlreadyExistsException;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    AdminController adminController;

    @Mock
    private BusService busService;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    public void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminlogin"));
    }

    @Test
    public void testGetAllBuses() throws Exception {
        List<BusDto> busDtos=List.of(
                new BusDto(1L,"Intrcity","AC Seater","8975",40,null),
                new BusDto(2L,"uni","AC Sleeper","8976",40,null)
        );

        when(busService.getAllBuses()).thenReturn(busDtos);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admindashboard"))
                .andExpect(model().attributeExists("buses"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowAddBusForm() throws Exception {
        mockMvc.perform(get("/admin/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("addbus"))
                .andExpect(model().attributeExists("busDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddBusAndSchedules() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,null);

        mockMvc.perform(post("/admin/add")
                        .flashAttr("busDto",busDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/admin/dashboard"));
        verify(busService).createBusWithSchedules(busDto);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddBusAndSchedules_ValidationErrors() throws Exception {
        BusDto busDto=new BusDto();

        mockMvc.perform(post("/admin/add")
                .flashAttr("busDto",busDto))
                .andExpect(status().isOk())
                .andExpect(view().name("addbus"))
                .andExpect(model().attributeExists("error"));

        verify(busService, never()).createBusWithSchedules(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddBusAndSchedules_BusAlreadyExistsException() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,null);

        doThrow(new BusAlreadyExistsException("Bus Already exists")).when(busService).createBusWithSchedules(busDto);

        mockMvc.perform(post("/admin/add")
                        .flashAttr("busDto",busDto))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("busDto", "busName"))
                .andExpect(view().name("addbus"));

        verify(busService,times(1)).createBusWithSchedules(busDto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowExistingSchedulesForm() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,null);
//        ScheduleDto scheduleDto1=new ScheduleDto(1L,1L,"Chennai","Bangalore", LocalDate.of(2025, 4, 29),
//                "10:00","10:30",600d,40);
//        ScheduleDto scheduleDto2=new ScheduleDto(2L,1L,"Bangalore","Chennai", LocalDate.of(2025, 4, 30),
//                "10:00","10:30",600d,40);
//
//        List<ScheduleDto> scheduleDtos=List.of(scheduleDto1,scheduleDto2);

        when(busService.getByBusId(1L)).thenReturn(busDto);

        mockMvc.perform(get("/admin/schedules/editExisting/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editExistingSchedules"))
                .andExpect(model().attributeExists("busDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowExistingSchedulesForm_BusNotFound() throws Exception {
        Long busId=1L;
        when(busService.getByBusId(busId)).thenThrow(new BusNotFoundException("Bus Not Found"));

        mockMvc.perform(get("/admin/schedules/editExisting/"+busId))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage","Bus not found with ID: " + busId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateExistingBusSchedules() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>());

        doNothing().when(busService).updateExistingSchedules(busDto);

        mockMvc.perform(post("/admin/schedules/updateExisting")
                        .flashAttr("busDto",busDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attribute("message", "Bus schedules updated successfully!"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateExistingBusSchedules_ValidationErrors() throws Exception {
        BusDto busDto=new BusDto();

        mockMvc.perform(post("/admin/schedules/updateExisting")
                        .flashAttr("busDto",busDto))
                .andExpect(status().isOk())
                .andExpect(view().name("editExistingSchedules"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateExistingBusSchedules_BusNotFoundException() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>());

        doThrow(new BusNotFoundException("Bus not found")).when(busService).updateExistingSchedules(busDto);

        mockMvc.perform(post("/admin/schedules/updateExisting")
                        .flashAttr("busDto",busDto))
                .andExpect(status().isOk())
                .andExpect(view().name("editExistingSchedules"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowAddSchedulesForm() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>());
        busDto.setSchedules(new ArrayList<>());
        when(busService.getByBusId(1L)).thenReturn(busDto);

        mockMvc.perform(get("/admin/schedules/add/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("busDto"))
                .andExpect(view().name("addSchedules"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowAddSchedulesForm_BusNotFound() throws Exception {
        Long busId=99L;

        doThrow(new BusNotFoundException("Bus not found")).when(busService).getByBusId(99L);

        mockMvc.perform(get("/admin/schedules/add/"+busId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Bus not found with ID: " + busId))
                .andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSaveBusSchedules() throws Exception {
        BusDto busDto=new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>());

        mockMvc.perform(post("/admin/schedules/addNew")
                .flashAttr("busDto",busDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSaveBusSchedules_ValidationErrors() throws Exception {
        BusDto busDto=new BusDto();

        mockMvc.perform(post("/admin/schedules/addNew")
                        .flashAttr("busDto",busDto))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("addSchedules"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSaveBusSchedules_BusNotFoundException() throws Exception {
        BusDto busDto = new BusDto(1L, "Intrcity", "AC Seater", "8975", 40, new ArrayList<>());
        doThrow(new BusNotFoundException("Bus not found")).when(busService).addNewSchedules(busDto);

        mockMvc.perform(post("/admin/schedules/addNew")
                        .flashAttr("busDto",busDto))
                .andExpect(status().isOk())
                .andExpect(view().name("addSchedules"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testViewSchedules() throws Exception {
        BusDto busDto = new BusDto(1L, "Intrcity", "AC Seater", "8975", 40, new ArrayList<>());

        when(busService.getByBusId(1L)).thenReturn(busDto);

        mockMvc.perform(get("/admin/schedules/view/"+busDto.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("busDto"))
                .andExpect(model().attributeExists("schedules"))
                .andExpect(view().name("viewSchedules"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testViewSchedules_BusNotFoundException() throws Exception {
        Long busId=99L;

        when(busService.getByBusId(busId)).thenThrow(new BusNotFoundException("Bus not found with ID: " + busId));

        mockMvc.perform(get("/admin/schedules/view/{busId}", busId))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "Bus not found with ID: " + busId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteBus() throws Exception {
        Long busId=1L;

        doNothing().when(busService).deleteBusAndSchedules(busId);

        mockMvc.perform(get("/admin/buses/delete/{busId}",busId))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteBus_BusNotFoundException() throws Exception {
        Long busId=1L;

        doThrow(new BusNotFoundException("Bus not found")).when(busService).deleteBusAndSchedules(busId);

        mockMvc.perform(get("/admin/buses/delete/{busId}",busId))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

}
