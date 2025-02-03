package com.example.ticketbookingsystem.dto;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A utility class for building QueryDSL predicates in the Ticket Booking System application.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QPredicates {

    private final List<Predicate> predicates = new ArrayList<>();

    public static QPredicates builder(){
        return new QPredicates();
    }

    /**
     * Adds a predicate to the list if the object is not null.
     *
     * @param object The object to check for nullity.
     * @param function The function to create the predicate.
     * @param <T> The type of the object.
     * @return The current QPredicates instance.
     */
    public <T> QPredicates add(T object, Function<T, Predicate> function){
        if(object != null){
            predicates.add(function.apply(object));
        }
        return this;
    }

    /**
     * Builds a combined predicate using logical AND of all predicates.
     *
     * @return The combined predicate.
     */
    public Predicate build(){
        return Optional.ofNullable(ExpressionUtils.allOf(predicates))
                .orElseGet(() -> Expressions.asBoolean(true).isTrue());
    }

    /**
     * Builds a combined predicate using logical OR of all predicates.
     *
     * @return The combined predicate.
     */
    public Predicate buildOr(){
        return Optional.ofNullable(ExpressionUtils.anyOf(predicates))
                .orElseGet(() -> Expressions.asBoolean(true).isTrue());
    }
}