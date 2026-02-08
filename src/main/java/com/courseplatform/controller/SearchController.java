package com.courseplatform.controller;

import com.courseplatform.dto.response.SearchResponse;
import com.courseplatform.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search courses and content")
public class SearchController {
    
    private final SearchService searchService;
    
    @GetMapping
    @Operation(summary = "Search courses", description = "Search across courses, topics, and subtopics")
    public ResponseEntity<SearchResponse> search(@RequestParam String q) {
        return ResponseEntity.ok(searchService.search(q));
    }
}
