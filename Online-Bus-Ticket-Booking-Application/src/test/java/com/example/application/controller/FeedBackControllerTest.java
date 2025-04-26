package com.example.application.controller;

import com.example.application.dto.FeedbackDto;
import com.example.application.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FeedBackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @BeforeEach
    void setUp(){
        mockMvc= MockMvcBuilders.standaloneSetup(feedbackController).build();
    }

    @Test
    public void testShowFeedbackForm() throws Exception {
        mockMvc.perform(get("/feedback/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("feedback"))
                .andExpect(model().attributeExists("feedbackDto"));
    }

    @Test
    public void testSubmitFeedback() throws Exception {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setName("John Doe");
        feedbackDto.setEmail("john@example.com");
        feedbackDto.setMessage("Great service!");

        doNothing().when(feedbackService).submitFeedback(feedbackDto);
          mockMvc.perform(post("/feedback/submit")
                          .flashAttr("feedbackDto",feedbackDto))
                  .andExpect(status().is3xxRedirection())
                  .andExpect(flash().attributeExists("message"))
                  .andExpect(redirectedUrl("/feedback/form"));
    }

    @Test
    public void testSubmitFeedback_ValidationErrors() throws Exception {
        FeedbackDto feedbackDto = new FeedbackDto();
        mockMvc.perform(post("/feedback/submit")
                        .flashAttr("feedbackDto",feedbackDto))
                .andExpect(status().isOk())
                .andExpect(view().name("feedback"));
    }

}
