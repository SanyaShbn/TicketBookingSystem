package com.example.ticketbookingsystem.utils;

import java.util.List;

public record FiltrationSqlQueryParameters(String sql, List<Object> parameters) {
}
