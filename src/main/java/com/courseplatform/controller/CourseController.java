package com.courseplatform.controller;

import com.courseplatform.dto.response.CourseDetailResponse;
import com.courseplatform.dto.response.CourseListResponse;
import com.courseplatform.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Public course browsing APIs")
public class CourseController {
    
    private final CourseService courseService;
    
    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve list of all available courses")
    public ResponseEntity<CourseListResponse> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    
    @GetMapping("/{courseId}")
    @Operation(summary = "Get course by ID", description = "Retrieve detailed information about a specific course")
    public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable String courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }
}
