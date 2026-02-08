package com.courseplatform.service;

import com.courseplatform.dto.response.*;
import com.courseplatform.entity.*;
import com.courseplatform.exception.NotFoundException;
import com.courseplatform.exception.UnauthorizedException;
import com.courseplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {
    
    private final SubtopicProgressRepository progressRepository;
    private final SubtopicRepository subtopicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    
    @Transactional
    public ProgressResponse markSubtopicComplete(String email, String subtopicId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Subtopic subtopic = subtopicRepository.findBySubtopicId(subtopicId)
                .orElseThrow(() -> new NotFoundException("Subtopic not found"));
        
        // Get course ID from subtopic
        String courseId = subtopic.getTopic().getCourse().getId();
        
        // Check if user is enrolled in the course
        if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new UnauthorizedException("You must be enrolled in this course to mark progress");
        }
        
        // Check if already completed (idempotent)
        Optional<SubtopicProgress> existingProgress = 
                progressRepository.findByUserIdAndSubtopicId(user.getId(), subtopic.getId());
        
        if (existingProgress.isPresent()) {
            SubtopicProgress progress = existingProgress.get();
            return ProgressResponse.builder()
                    .subtopicId(subtopicId)
                    .completed(true)
                    .completedAt(progress.getCompletedAt())
                    .build();
        }
        
        // Create new progress
        SubtopicProgress progress = SubtopicProgress.builder()
                .user(user)
                .subtopic(subtopic)
                .completed(true)
                .build();
        
        progress = progressRepository.save(progress);
        
        return ProgressResponse.builder()
                .subtopicId(subtopicId)
                .completed(true)
                .completedAt(progress.getCompletedAt())
                .build();
    }
    
    @Transactional(readOnly = true)
    public EnrollmentProgressResponse getEnrollmentProgress(Long enrollmentId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found"));
        
        // Verify ownership
        if (!enrollment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only view your own progress");
        }
        
        Course course = enrollment.getCourse();
        String courseId = course.getId();
        
        // Count total subtopics
        int totalSubtopics = course.getTopics().stream()
                .mapToInt(topic -> topic.getSubtopics().size())
                .sum();
        
        // Get completed subtopics
        List<SubtopicProgress> allProgress = 
                progressRepository.findByUserIdAndCourseId(user.getId(), courseId);
        
        List<SubtopicProgress> completedProgress = allProgress.stream()
                .filter(SubtopicProgress::isCompleted)
                .collect(Collectors.toList());
        
        int completedSubtopics = completedProgress.size();
        double completionPercentage = totalSubtopics > 0 
                ? (completedSubtopics * 100.0) / totalSubtopics 
                : 0.0;
        
        List<CompletedItemDTO> completedItems = completedProgress.stream()
                .map(progress -> CompletedItemDTO.builder()
                        .subtopicId(progress.getSubtopic().getSubtopicId())
                        .subtopicTitle(progress.getSubtopic().getTitle())
                        .completedAt(progress.getCompletedAt())
                        .build())
                .collect(Collectors.toList());
        
        return EnrollmentProgressResponse.builder()
                .enrollmentId(enrollmentId)
                .courseId(courseId)
                .courseTitle(course.getTitle())
                .totalSubtopics(totalSubtopics)
                .completedSubtopics(completedSubtopics)
                .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                .completedItems(completedItems)
                .build();
    }
}
