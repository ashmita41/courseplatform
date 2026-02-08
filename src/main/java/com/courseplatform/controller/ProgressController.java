package com.courseplatform.controller;

import com.courseplatform.dto.response.EnrollmentProgressResponse;
import com.courseplatform.dto.response.ProgressResponse;
import com.courseplatform.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Learning progress tracking APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ProgressController {
    
    private final ProgressService progressService;
    
    @PostMapping("/subtopics/{subtopicId}/complete")
    @Operation(summary = "Mark subtopic as completed", description = "Mark a subtopic as completed for the authenticated user")
    public ResponseEntity<ProgressResponse> markSubtopicComplete(
            @PathVariable String subtopicId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(progressService.markSubtopicComplete(userDetails.getUsername(), subtopicId));
    }
    
    @GetMapping("/enrollments/{enrollmentId}/progress")
    @Operation(summary = "View enrollment progress", description = "Get detailed progress for a specific enrollment")
    public ResponseEntity<EnrollmentProgressResponse> getEnrollmentProgress(
            @PathVariable Long enrollmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(progressService.getEnrollmentProgress(enrollmentId, userDetails.getUsername()));
    }
}
