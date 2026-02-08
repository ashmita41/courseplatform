package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseListDTO {
    private String id;
    private String title;
    private String description;
    private int topicCount;
    private int subtopicCount;
}
