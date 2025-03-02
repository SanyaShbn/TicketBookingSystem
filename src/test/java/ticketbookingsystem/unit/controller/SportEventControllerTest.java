package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.SportEventController;
import com.example.ticketbookingsystem.dto.PageResponse;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.service.SportEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SportEventControllerTest {

    private static final Long SPORT_EVENT_ID = 1L;

    private static final String ARENA_NAME = "Test_Arena";

    @Mock
    private SportEventService sportEventService;

    @InjectMocks
    private SportEventController sportEventController;

    @Test
    public void testFindAllSportEvents() {
        SportEventReadDto event1 = SportEventReadDto.builder().eventName("Event1").build();
        SportEventReadDto event2 = SportEventReadDto.builder().eventName("Event2").build();

        List<SportEventReadDto> events = Arrays.asList(event1, event2);
        Page<SportEventReadDto> page = new PageImpl<>(events);
        when(sportEventService.findAll(any(SportEventFilter.class), any(Pageable.class))).thenReturn(page);

        PageResponse<SportEventReadDto> response = sportEventController.findAllSportEvents(
                new SportEventFilter(LocalDateTime.now(), LocalDateTime.now(), ARENA_NAME, ""),
                Pageable.unpaged());

        assertEquals(2, response.getContent().size());
        verify(sportEventService, times(1)).findAll(
                any(SportEventFilter.class), any(Pageable.class));
    }

    @Test
    void testGetSportEventById() {
        SportEventReadDto event = SportEventReadDto.builder().eventName("Event1").build();
        when(sportEventService.findById(SPORT_EVENT_ID)).thenReturn(Optional.of(event));

        ResponseEntity<SportEventReadDto> response = sportEventController.getSportEventById(SPORT_EVENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event1", response.getBody().getEventName());
        verify(sportEventService, times(1)).findById(SPORT_EVENT_ID);
    }

    @Test
    public void testCreateSportEvent() {
        SportEventCreateEditDto createEditDto = SportEventCreateEditDto.builder().eventName("New Event").build();

        SportEventReadDto readDto = SportEventReadDto.builder().eventName("New Event").build();
        when(sportEventService.createSportEvent(any(SportEventCreateEditDto.class), any(Long.class)))
                .thenReturn(readDto);

        ResponseEntity<SportEventReadDto> response = sportEventController
                .createSportEvent(SPORT_EVENT_ID, createEditDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Event", response.getBody().getEventName());
        verify(sportEventService, times(1))
                .createSportEvent(any(SportEventCreateEditDto.class), any(Long.class));
    }

    @Test
    public void testUpdateSportEvent() {
        SportEventCreateEditDto createEditDto = SportEventCreateEditDto.builder().eventName("Updated Event").build();

        SportEventReadDto readDto = SportEventReadDto.builder().eventName("Updated Event").build();
        when(sportEventService.updateSportEvent(eq(SPORT_EVENT_ID),
                any(SportEventCreateEditDto.class),
                any(Long.class)))
                .thenReturn(readDto);

        ResponseEntity<SportEventReadDto> response = sportEventController.updateSportEvent(
                SPORT_EVENT_ID, SPORT_EVENT_ID, createEditDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Event", response.getBody().getEventName());
        verify(sportEventService, times(1)).updateSportEvent(
                eq(SPORT_EVENT_ID), any(SportEventCreateEditDto.class), any(Long.class));
    }

    @Test
    public void testDeleteSportEvent() {
        doNothing().when(sportEventService).deleteSportEvent(SPORT_EVENT_ID);

        ResponseEntity<Map<String, String>> response = sportEventController.deleteSportEvent(SPORT_EVENT_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sporting event deleted successfully", response.getBody().get("message"));
        verify(sportEventService, times(1)).deleteSportEvent(SPORT_EVENT_ID);
    }

}