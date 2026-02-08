package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtopicProgressResponse {
    private Long subtopicId;
    private String subtopicTitle;
    private Boolean completed;
    private LocalDateTime lastAccessed;
}
