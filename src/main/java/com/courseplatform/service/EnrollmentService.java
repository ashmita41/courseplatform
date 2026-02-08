package com.courseplatform.service;

import com.courseplatform.dto.response.EnrollmentResponse;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Enrollment;
import com.courseplatform.entity.User;
import com.courseplatform.exception.ConflictException;
import com.courseplatform.exception.NotFoundException;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.EnrollmentRepository;
import com.courseplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    
    @Transactional
    public EnrollmentResponse enrollUser(String email, String courseId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        
        // Check if already enrolled
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new ConflictException("You are already enrolled in this course");
        }
        
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .build();
        
        enrollment = enrollmentRepository.save(enrollment);
        
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}
