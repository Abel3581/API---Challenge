package com.intuit.challange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse <T> {
    private List <T> content;
    private PageMetadata page;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageMetadata {
        private int size;
        private long totalElements;
        private int totalPages;
        private int number;
    }
}
