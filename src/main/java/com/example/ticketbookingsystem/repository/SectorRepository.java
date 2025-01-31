package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.Sector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {

    Page<Sector> findAllByArenaId(Long arenaId, Pageable sortedPageable);

    @Modifying
    @Query("UPDATE Arena a SET a.generalSeatsNumb = a.generalSeatsNumb + :seatsNumb WHERE a.id = :arenaId")
    void updateArenaAfterSectorSave(Long arenaId, int seatsNumb);

    @Modifying
    @Query("UPDATE Arena a SET a.generalSeatsNumb = a.generalSeatsNumb - :oldSeatsNumb + :newSeatsNumb WHERE a.id = :arenaId")
    void updateArenaBeforeSectorUpdate(Long arenaId, int oldSeatsNumb, int newSeatsNumb);

    @Modifying
    @Query("UPDATE Arena a SET a.generalSeatsNumb = a.generalSeatsNumb - :seatsNumb WHERE a.id = :arenaId")
    void updateArenaAfterSectorDelete(Long arenaId, int seatsNumb);
}
