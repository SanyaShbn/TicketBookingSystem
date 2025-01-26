package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RowRepository extends JpaRepository<Row, Long> {

    List<Row> findAllBySectorId(Long sectorId);

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