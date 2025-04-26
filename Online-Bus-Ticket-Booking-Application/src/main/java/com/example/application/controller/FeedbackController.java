package com.example.application.controller;

import com.example.application.dto.FeedbackDto;
import com.example.application.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/form")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedbackDto", new FeedbackDto());
        return "feedback";
    }

    @PostMapping("/submit")
    public String submitFeedback(@Valid @ModelAttribute("feedbackDto") FeedbackDto feedbackDto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "feedback";
        }

        feedbackService.submitFeedback(feedbackDto);
        redirectAttributes.addFlashAttribute("message", "Thank you for your feedback!");
        return "redirect:/feedback/form";
    }

}