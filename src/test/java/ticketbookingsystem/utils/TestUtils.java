package ticketbookingsystem.utils;

import com.example.ticketbookingsystem.dto.ticket_dto.TicketCreateEditDto;
import com.example.ticketbookingsystem.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestUtils {

    public static TicketCreateEditDto buildTicketCreateEditDto(){
        return TicketCreateEditDto.builder()
                .status(TicketStatus.AVAILABLE)
                .price(BigDecimal.valueOf(10))
                .build();
    }

    public static Arena buildArena(){
        return Arena.builder()
                .name("Test Arena")
                .city("Test City")
                .capacity(1)
                .build();
    }

    public static SportEvent buildSportEvent(Arena savedArena){
        return SportEvent.builder()
                .eventName("Test Event")
                .eventDateTime(LocalDateTime.now())
                .arena(savedArena)
                .build();
    }

    public static Sector buildSector(Arena arena){
        return Sector.builder()
                .sectorName("Test Sector")
                .maxRowsNumb(10)
                .maxSeatsNumb(100)
                .arena(arena)
                .build();
    }

    public static Row buildRow(Sector sector) {
        return Row.builder()
                .rowNumber(1)
                .seatsNumb(5)
                .sector(sector)
                .build();
    }

    public static Seat buildSeat(Row row){
        return Seat.builder()
                .seatNumber(1)
                .row(row)
                .build();
    }

}
