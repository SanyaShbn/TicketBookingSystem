package com.example.ticketbookingsystem.dto;

import lombok.Value;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A generic class representing a paginated response.
 *
 * @param <T> The type of content in the page.
 */
@Value
public class PageResponse<T> {

    List<T> content;

    Metadata metadata;

    /**
     * Creates a PageResponse from a Page object.
     *
     * @param page The Page object.
     * @param <T> The type of content in the page.
     * @return A PageResponse containing the content and metadata.
     */
    public static <T> PageResponse<T> of(Page<T> page){
        var metadata = new Metadata(page.getNumber(), page.getSize(), page.getTotalElements());
        return new PageResponse<>(page.getContent(), metadata);
    }

    /**
     * A static class representing the metadata of a paginated response.
     */
    @Value
    public static class Metadata{
        int page;
        int size;
        long totalElements;
    }
}