package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.QSportEvent;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Implementation of CustomSportEventRepository.
 */
public class CustomSportEventRepositoryImpl implements CustomSportEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Finds all SportEvents with associated arenas based on a predicate and pageable information.
     *
     * @param predicate The predicate for filtering.
     * @param pageable The pagination information.
     * @return A list of SportEvents.
     */
    @Override
    public List<SportEvent> findAllWithArena(Predicate predicate, Pageable pageable) {
        JPAQuery<SportEvent> query = new JPAQuery<>(entityManager);
        QSportEvent sportEvent = QSportEvent.sportEvent;

        for (Sort.Order order : pageable.getSort()) {
            OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sportEvent, order);
            if (orderSpecifier != null) {
                query.orderBy(orderSpecifier);
            }
        }

        return query
                .select(sportEvent)
                .from(sportEvent)
                .leftJoin(sportEvent.arena).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(QSportEvent sportEvent, Sort.Order order) {
        if (order.getProperty().equals("eventDateTime")) {
            return order.isAscending() ? sportEvent.eventDateTime.asc() : sportEvent.eventDateTime.desc();
        }
        return null;
    }
}