package com.example.application.service.impl;

import com.example.application.dto.FeedbackDto;
import com.example.application.entity.Feedback;
import com.example.application.repository.FeedbackRepository;
import com.example.application.service.FeedbackService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void submitFeedback(FeedbackDto feedbackDto) {
        Optional<Feedback> existingFeedback = feedbackRepository.findByEmail(feedbackDto.getEmail());

        if (existingFeedback.isPresent()) {
            // Update existing feedback
            Feedback feedbackToUpdate = existingFeedback.get();
            feedbackToUpdate.setName(feedbackDto.getName());
            feedbackToUpdate.setSubject(feedbackDto.getSubject());
            feedbackToUpdate.setMessage(feedbackDto.getMessage());
            // The submittedAt will be updated automatically upon saving
            feedbackRepository.save(feedbackToUpdate);
        } else {
            // Save new feedback
            Feedback feedback = modelMapper.map(feedbackDto, Feedback.class);
            feedbackRepository.save(feedback);
        }
    }
}
