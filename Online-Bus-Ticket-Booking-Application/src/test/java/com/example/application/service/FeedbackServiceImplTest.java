package com.example.application.service;

import com.example.application.dto.FeedbackDto;
import com.example.application.entity.Feedback;
import com.example.application.repository.FeedbackRepository;
import com.example.application.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    public void testSubmitFeedback_ShouldUpdateExistingFeedback_WhenEmailExists() {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setEmail("test@example.com");
        feedbackDto.setName("Updated Name");
        feedbackDto.setSubject("Updated Subject");
        feedbackDto.setMessage("Updated Message");

        Feedback existingFeedback = new Feedback();
        existingFeedback.setEmail("test@example.com");
        existingFeedback.setName("Old Name");
        existingFeedback.setSubject("Old Subject");
        existingFeedback.setMessage("Old Message");

        when(feedbackRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingFeedback));

        feedbackService.submitFeedback(feedbackDto);

        assertThat(existingFeedback.getName()).isEqualTo("Updated Name");
        assertThat(existingFeedback.getSubject()).isEqualTo("Updated Subject");
        assertThat(existingFeedback.getMessage()).isEqualTo("Updated Message");

        verify(feedbackRepository, times(1)).save(existingFeedback);
    }

    @Test
    void testSubmitFeedback_ShouldSaveNewFeedback_WhenEmailDoesNotExist() {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setEmail("newuser@example.com");
        feedbackDto.setName("New User");
        feedbackDto.setSubject("New Subject");
        feedbackDto.setMessage("This is a new message.");

        Feedback mappedFeedback = new Feedback();
        mappedFeedback.setEmail("newuser@example.com");
        mappedFeedback.setName("New User");
        mappedFeedback.setSubject("New Subject");
        mappedFeedback.setMessage("This is a new message.");

        when(feedbackRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(modelMapper.map(feedbackDto, Feedback.class)).thenReturn(mappedFeedback);

        feedbackService.submitFeedback(feedbackDto);

        verify(feedbackRepository, times(1)).save(mappedFeedback);
    }


}
