package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Row entities.
 */
@Repository
public interface RowRepository extends JpaRepository<Row, Long> {

    @Query("FROM Row r JOIN FETCH r.sector s JOIN FETCH s.arena WHERE r.sector.id = :sectorId")
    Page<Row> findAllBySectorId(Long sectorId, Pageable sortedPageable);

    @Query("SELECT r FROM Row r JOIN FETCH r.sector s JOIN FETCH s.arena WHERE r.id = :id")
    Optional<Row> findById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Sector s SET s.availableRowsNumb = s.availableRowsNumb + 1, " +
            "s.availableSeatsNumb = s.availableSeatsNumb + :seatsNumb WHERE s.id = :sectorId")
    void updateSectorAfterRowSave(Long sectorId, int seatsNumb);

    @Modifying
    @Query("UPDATE Sector s SET s.availableSeatsNumb = " +
            "s.availableSeatsNumb - :oldSeatsNumb + :newSeatsNumb WHERE s.id = :sectorId")
    void updateSectorBeforeRowUpdate(Long sectorId, int oldSeatsNumb, int newSeatsNumb);

    @Modifying
    @Query("UPDATE Sector s SET s.availableRowsNumb = s.availableRowsNumb - 1, " +
            "s.availableSeatsNumb = s.availableSeatsNumb - :seatsNumb WHERE s.id = :sectorId")
    void updateSectorAfterRowDelete(Long sectorId, int seatsNumb);
}