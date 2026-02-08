package com.courseplatform.controller;

import com.courseplatform.dto.response.EnrollmentResponse;
import com.courseplatform.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "Course enrollment APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    @PostMapping("/{courseId}/enroll")
    @Operation(summary = "Enroll in course", description = "Enroll the authenticated user in a course")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(
            @PathVariable String courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(enrollmentService.enrollUser(userDetails.getUsername(), courseId));
    }
}
