package ticketbookingsystem.utils;

import com.example.ticketbookingsystem.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestUtils {

    public static Arena createTestArena() {
        return Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(5000)
                .generalSeatsNumb(1000)
                .build();
    }

    public static Arena createTestArena(String name, String city, int capacity, int generalSeatsNumb) {
        return Arena.builder()
                .name(name)
                .city(city)
                .capacity(capacity)
                .generalSeatsNumb(generalSeatsNumb)
                .build();
    }

    public static Sector createTestSector(Arena arena) {
        return Sector.builder()
                .sectorName("Test Sector")
                .arena(arena)
                .build();
    }

    public static Row createTestRow(Sector sector) {
        return Row.builder()
                .rowNumber(1)
                .sector(sector)
                .build();
    }

    public static Seat createTestSeat(Row row) {
        return Seat.builder()
                .row(row)
                .seatNumber(1)
                .build();
    }

    public static SportEvent createTestSportEvent(String eventName, LocalDateTime eventDateTime, Arena arena) {
        return SportEvent.builder()
                .eventName(eventName)
                .eventDateTime(eventDateTime)
                .arena(arena)
                .build();
    }

    public static Ticket createTestTicket(SportEvent sportEvent, Seat seat) {
        return Ticket.builder()
                .price(BigDecimal.valueOf(30))
                .status(TicketStatus.AVAILABLE)
                .sportEvent(sportEvent)
                .seat(seat)
                .build();
    }

}
