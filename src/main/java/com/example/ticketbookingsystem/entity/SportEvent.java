package com.example.ticketbookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a sporting event which users may want to visit and buy tickets for
 */
@Entity
@Table(name = "sport_event")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SportEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "event_date_time", nullable = false)
    private LocalDateTime eventDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arena_id", nullable = false)
    private Arena arena;

    @Column(name = "poster_image")
    private String posterImage;
}
