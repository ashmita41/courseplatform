package com.courseplatform.repository;

import com.courseplatform.entity.SubtopicProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubtopicProgressRepository extends JpaRepository<SubtopicProgress, Long> {
    
    Optional<SubtopicProgress> findByUserIdAndSubtopicId(Long userId, Long subtopicId);
    
    @Query("SELECT sp FROM SubtopicProgress sp " +
           "JOIN sp.subtopic s " +
           "JOIN s.topic t " +
           "WHERE sp.user.id = :userId AND t.course.id = :courseId")
    List<SubtopicProgress> findByUserIdAndCourseId(
        @Param("userId") Long userId, 
        @Param("courseId") String courseId
    );
}
