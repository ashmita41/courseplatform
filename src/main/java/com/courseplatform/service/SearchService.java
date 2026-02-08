package com.courseplatform.service;

import com.courseplatform.dto.response.*;
import com.courseplatform.entity.Course;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.Topic;
import com.courseplatform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final CourseRepository courseRepository;
    
    @Transactional(readOnly = true)
    public SearchResponse search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return SearchResponse.builder()
                    .query(query)
                    .results(new ArrayList<>())
                    .build();
        }
        
        List<Course> courses = courseRepository.searchCourses(query);
        List<SearchResultDTO> results = new ArrayList<>();
        
        Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        
        for (Course course : courses) {
            List<MatchDTO> matches = new ArrayList<>();
            
            // Search in course title and description
            if (pattern.matcher(course.getTitle()).find()) {
                matches.add(MatchDTO.builder()
                        .type("course")
                        .snippet(course.getTitle())
                        .build());
            }
            
            if (pattern.matcher(course.getDescription()).find()) {
                matches.add(MatchDTO.builder()
                        .type("course")
                        .snippet(truncate(course.getDescription(), query))
                        .build());
            }
            
            // Search in topics and subtopics
            for (Topic topic : course.getTopics()) {
                if (pattern.matcher(topic.getTitle()).find()) {
                    matches.add(MatchDTO.builder()
                            .type("topic")
                            .topicTitle(topic.getTitle())
                            .snippet(topic.getTitle())
                            .build());
                }
                
                for (Subtopic subtopic : topic.getSubtopics()) {
                    if (pattern.matcher(subtopic.getTitle()).find()) {
                        matches.add(MatchDTO.builder()
                                .type("subtopic")
                                .topicTitle(topic.getTitle())
                                .subtopicId(subtopic.getSubtopicId())
                                .subtopicTitle(subtopic.getTitle())
                                .snippet(subtopic.getTitle())
                                .build());
                    }
                    
                    if (pattern.matcher(subtopic.getContent()).find()) {
                        matches.add(MatchDTO.builder()
                                .type("content")
                                .topicTitle(topic.getTitle())
                                .subtopicId(subtopic.getSubtopicId())
                                .subtopicTitle(subtopic.getTitle())
                                .snippet(truncate(subtopic.getContent(), query))
                                .build());
                    }
                }
            }
            
            if (!matches.isEmpty()) {
                results.add(SearchResultDTO.builder()
                        .courseId(course.getId())
                        .courseTitle(course.getTitle())
                        .matches(matches)
                        .build());
            }
        }
        
        return SearchResponse.builder()
                .query(query)
                .results(results)
                .build();
    }
    
    private String truncate(String text, String query) {
        if (text == null || text.length() <= 150) {
            return text;
        }
        
        int index = text.toLowerCase().indexOf(query.toLowerCase());
        if (index == -1) {
            return text.substring(0, 150) + "...";
        }
        
        int start = Math.max(0, index - 50);
        int end = Math.min(text.length(), index + query.length() + 100);
        
        String snippet = text.substring(start, end);
        if (start > 0) snippet = "..." + snippet;
        if (end < text.length()) snippet = snippet + "...";
        
        return snippet;
    }
}
