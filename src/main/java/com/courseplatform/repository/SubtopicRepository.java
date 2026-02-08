package com.courseplatform.repository;

import com.courseplatform.entity.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, Long> {
    Optional<Subtopic> findBySubtopicId(String subtopicId);
}
