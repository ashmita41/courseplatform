package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicProgressResponse {
    private Long topicId;
    private String topicTitle;
    private Double progress;
    private List<SubtopicProgressResponse> subtopicProgress;
}
