package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private String type; // "subtopic", "content", "course", "topic"
    private String topicTitle;
    private String subtopicId;
    private String subtopicTitle;
    private String snippet;
}
