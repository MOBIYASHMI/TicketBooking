package com.example.application.restController;

import com.example.application.dto.FeedbackDto;
import com.example.application.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackRestController {

    @Autowired
    private FeedbackService feedbackService;

    // Submit feedback via POST
    @PostMapping("/submit")
    public ResponseEntity<String> submitFeedback(@Valid @RequestBody FeedbackDto feedbackDto) {
        feedbackService.submitFeedback(feedbackDto);
        return ResponseEntity.ok("Thank you for your feedback!");
    }
}
