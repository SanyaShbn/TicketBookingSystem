package com.example.ticketbookingsystem.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FilterParameters<T> {

    private final Class<T> entityClass;

    private Map<String, Object> filters = new HashMap<>();

    private List<String> sortOrders = new ArrayList<>();

    public void addFilter(String field, Object value) {
        filters.put(field, value);
    }

    public void addSortOrder(String field, String order) {
        sortOrders.add(field + " " + order);
    }
}
