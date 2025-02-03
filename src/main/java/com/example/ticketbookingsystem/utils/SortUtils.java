package com.example.ticketbookingsystem.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Map;

/**
 * Utility class for building Sort objects for queries.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SortUtils {

    /**
     * Builds a Sort object from a map of field names and sort orders.
     *
     * @param sortOrders A map of field names and sort orders.
     * @return The Sort object.
     */
    public static Sort buildSort(Map<String, String> sortOrders) {
        Sort sort = Sort.unsorted();

        for (Map.Entry<String, String> entry : sortOrders.entrySet()) {
            String field = entry.getKey();
            String sortOrder = entry.getValue();
            if (sortOrder != null && !sortOrder.isEmpty()) {
                sort = addSortOrder(sort, field, sortOrder);
            }
        }

        return sort;
    }

    private static Sort addSortOrder(Sort currentSort, String field, String sortOrder) {
        Sort.Order order = createSortOrder(field, sortOrder);
        return currentSort.and(Sort.by(order));
    }

    private static Sort.Order createSortOrder(String field, String sortOrder) {
        return "ASC".equalsIgnoreCase(sortOrder) ? Sort.Order.asc(field) : Sort.Order.desc(field);
    }
}