package com.example.application.controller;


import com.example.application.dto.BusDto;
import com.example.application.exceptions.BusNotFoundException;
import com.example.application.service.BusService;
import com.example.application.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BusService busService;

    @Mock
    private ScheduleService scheduleService;

    @InjectMocks
    private BusController busController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(busController).build();
    }

    @Test
    public void testGetAllBuses() throws Exception {
        List<BusDto> buses=List.of(
                new BusDto(1L,"Intrcity","AC Seater","8975",40,null),
                new BusDto(2L,"uni","AC Sleeper","8976",40,null)
        );

        when(busService.getAllBuses()).thenReturn(buses);

        mockMvc.perform(get("/buses/viewAllBuses"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buses",buses))
                .andExpect(view().name("busList"));
    }

    @Test
    public void testGetBusById() throws Exception {
        Long busId=1L;
        BusDto bus=new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>());

        when(busService.getByBusId(1L)).thenReturn(bus);

        mockMvc.perform(get("/buses/"+busId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("bus",bus))
                .andExpect(view().name("seatBooking"));
    }

    @Test
    public void testGetBusById_BusNotFoundException() throws Exception {
        Long busId=1L;
        BusDto bus=new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>());

        doThrow(new BusNotFoundException("Bus not found")).when(busService).getByBusId(busId);

        mockMvc.perform(get("/buses/"+busId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "Bus not found with ID: " + busId))
                .andExpect(view().name("busList"));
    }

    @Test
    public void testViewBusByType() throws Exception {
        String busType="AC Seater";
        List<BusDto> buses=List.of(
                new BusDto(1L,"Intrcity","AC Seater","8975",40,new ArrayList<>()),
                new BusDto(2L,"uni","AC Seater","8976",40,new ArrayList<>())
        );

        when(busService.viewByBusType(busType)).thenReturn(buses);

        mockMvc.perform(get("/buses/viewBuses")
                        .param("busType",busType))
                .andExpect(status().isOk())
                .andExpect(model().attribute("buses",buses))
                .andExpect(model().attribute("busType",busType))
                .andExpect(view().name("busList"));
    }

}
