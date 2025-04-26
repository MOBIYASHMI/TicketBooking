package com.example.application.repository;

import com.example.application.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    Optional<Feedback> findByEmail(String email);
}
