package com.example.ticketbookingsystem.utils;

import java.util.List;

/**
 * Record for keeping and transfer entities filtration parameters.
 */
public record FiltrationSqlQueryParameters(String sql, List<Object> parameters) {
}
