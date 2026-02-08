package com.courseplatform.loader;

import com.courseplatform.entity.Course;
import com.courseplatform.entity.Subtopic;
import com.courseplatform.entity.Topic;
import com.courseplatform.entity.User;
import com.courseplatform.repository.CourseRepository;
import com.courseplatform.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create default users if they don't exist
        if (userRepository.count() == 0) {
            log.info("Creating default users...");
            Set<String> userRoles = new HashSet<>();
            userRoles.add("USER"); // User entity adds "ROLE_" prefix in getAuthorities()
            User user = User.builder()
                    .email("user@example.com")
                    .password(passwordEncoder.encode("password"))
                    .roles(userRoles)
                    .build();
            userRepository.save(user);

            Set<String> adminRoles = new HashSet<>();
            adminRoles.add("ADMIN"); // User entity adds "ROLE_" prefix in getAuthorities()
            User admin = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("adminpass"))
                    .roles(adminRoles)
                    .build();
            userRepository.save(admin);
            log.info("Default users created.");
        }
        
        if (courseRepository.count() == 0) {
            log.info("Database is empty. Loading seed data...");
            loadSeedData();
            log.info("Seed data loaded successfully!");
        } else {
            log.info("Database already contains data. Skipping seed data loading.");
        }
    }
    
    private void loadSeedData() throws IOException {
        ClassPathResource resource = new ClassPathResource("seed-data.json");
        
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode coursesNode = root.get("courses");
            
            for (JsonNode courseNode : coursesNode) {
                Course course = parseCourse(courseNode);
                courseRepository.save(course);
                log.info("Loaded course: {}", course.getTitle());
            }
        }
    }
    
    private Course parseCourse(JsonNode courseNode) {
        Course course = Course.builder()
                .id(courseNode.get("id").asText())
                .title(courseNode.get("title").asText())
                .description(courseNode.get("description").asText())
                .build();
        
        JsonNode topicsNode = courseNode.get("topics");
        if (topicsNode != null && topicsNode.isArray()) {
            for (JsonNode topicNode : topicsNode) {
                Topic topic = parseTopic(topicNode);
                course.addTopic(topic);
            }
        }
        
        return course;
    }
    
    private Topic parseTopic(JsonNode topicNode) {
        Topic topic = Topic.builder()
                .topicId(topicNode.get("id").asText())
                .title(topicNode.get("title").asText())
                .build();
        
        JsonNode subtopicsNode = topicNode.get("subtopics");
        if (subtopicsNode != null && subtopicsNode.isArray()) {
            for (JsonNode subtopicNode : subtopicsNode) {
                Subtopic subtopic = parseSubtopic(subtopicNode);
                topic.addSubtopic(subtopic);
            }
        }
        
        return topic;
    }
    
    private Subtopic parseSubtopic(JsonNode subtopicNode) {
        return Subtopic.builder()
                .subtopicId(subtopicNode.get("id").asText())
                .title(subtopicNode.get("title").asText())
                .content(subtopicNode.get("content").asText())
                .build();
    }
}
