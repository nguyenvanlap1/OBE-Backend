package com.OBE.workflow.conmon.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ hiện những trường có dữ liệu
public class PageResponse<T, U> {
    private List<U> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T, U> PageResponse<T, U> fromPage(Page<T> page, List<U> responseObjects) {
        return PageResponse.<T, U>builder()
                .content(responseObjects)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
    public static <U> PageResponse<U, U> fromPage(Page<U> page) {
        return PageResponse.<U, U>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
