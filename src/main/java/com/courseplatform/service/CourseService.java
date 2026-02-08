package com.courseplatform.service;

import com.courseplatform.dto.response.*;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.Topic;
import com.courseplatform.exception.NotFoundException;
import com.courseplatform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    @Transactional(readOnly = true)
    public CourseListResponse getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        
        List<CourseListDTO> courseDTOs = courses.stream()
                .map(this::convertToCourseListDTO)
                .collect(Collectors.toList());
        
        return CourseListResponse.builder()
                .courses(courseDTOs)
                .build();
    }
    
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
        
        return convertToCourseDetailResponse(course);
    }
    
    private CourseListDTO convertToCourseListDTO(Course course) {
        int topicCount = course.getTopics().size();
        int subtopicCount = course.getTopics().stream()
                .mapToInt(topic -> topic.getSubtopics().size())
                .sum();
        
        return CourseListDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .topicCount(topicCount)
                .subtopicCount(subtopicCount)
                .build();
    }
    
    private CourseDetailResponse convertToCourseDetailResponse(Course course) {
        List<TopicDTO> topicDTOs = course.getTopics().stream()
                .map(this::convertToTopicDTO)
                .collect(Collectors.toList());
        
        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .topics(topicDTOs)
                .build();
    }
    
    private TopicDTO convertToTopicDTO(Topic topic) {
        List<SubtopicDTO> subtopicDTOs = topic.getSubtopics().stream()
                .map(this::convertToSubtopicDTO)
                .collect(Collectors.toList());
        
        return TopicDTO.builder()
                .id(topic.getTopicId())
                .title(topic.getTitle())
                .subtopics(subtopicDTOs)
                .build();
    }
    
    private SubtopicDTO convertToSubtopicDTO(Subtopic subtopic) {
        return SubtopicDTO.builder()
                .id(subtopic.getSubtopicId())
                .title(subtopic.getTitle())
                .content(subtopic.getContent())
                .build();
    }
}
