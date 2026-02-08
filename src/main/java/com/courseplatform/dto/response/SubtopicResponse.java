package com.courseplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtopicResponse {
    private Long id;
    private String title;
    private String content;
    private Boolean completed;
}
