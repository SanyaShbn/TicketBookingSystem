package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.entity.SportEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing Sport Event entities.
 */
@Repository
public interface SportEventRepository extends JpaRepository<SportEvent, Long>, CustomSportEventRepository {

    @Query("SELECT se FROM SportEvent se JOIN FETCH se.arena WHERE se.id = :id")
    Optional<SportEvent> findById(@Param("id") Long id);

    boolean existsByPosterImage(String posterImage);

    @Modifying
    @Query("UPDATE SportEvent e SET e.posterImage = NULL WHERE e.posterImage = :filename")
    void updatePosterImagesAfterImageDeleting(@Param("filename") String filename);
}