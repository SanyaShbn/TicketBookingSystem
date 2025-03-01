package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventReadDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.entity.QSportEvent;
import com.example.ticketbookingsystem.entity.SportEvent;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventCreateEditMapper;
import com.example.ticketbookingsystem.mapper.sport_event_mapper.SportEventReadMapper;
import com.example.ticketbookingsystem.repository.SportEventRepository;
import com.example.ticketbookingsystem.utils.SortUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing sporting events.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SportEventService {

    private static final String SORT_BY_EVENT_DATE_TIME = "eventDateTime";

    private final SportEventRepository sportEventRepository;

    private final SportEventReadMapper sportEventReadMapper;

    private final SportEventCreateEditMapper sportEventCreateEditMapper;

    private final ArenaService arenaService;

    private final RestTemplate restTemplate;

    private final String IMAGE_SERVICE_URL = "http://localhost:8081/api/v1/images";

    /**
     * Finds all sporting events.
     *
     * @return a list of all sporting events
     */
    public List<SportEventReadDto> findAll(){
        return sportEventRepository.findAll().stream()
                .map(sportEventReadMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds all sporting events matching the given filter.
     *
     * @param sportEventFilter the filter to apply
     * @param pageable object of Pageable interface to apply pagination correctly
     * @return a list of sporting events matching the filter
     */
    public Page<SportEventReadDto> findAll(SportEventFilter sportEventFilter, Pageable pageable){
        Predicate predicate = toPredicate(sportEventFilter);

        Map<String, String> sortOrders = new LinkedHashMap<>();
        if (sportEventFilter.sortOrder() != null && !sportEventFilter.sortOrder().isEmpty()) {
            sortOrders.put(SORT_BY_EVENT_DATE_TIME, sportEventFilter.sortOrder());
        }

        Sort sort = SortUtils.buildSort(sortOrders);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return sportEventRepository.findAllWithArena(predicate, sortedPageable)
                .map(sportEventReadMapper::toDto);
    }

    private Predicate toPredicate(SportEventFilter sportEventFilter) {
        BooleanBuilder builder = new BooleanBuilder();
        QSportEvent sportEvent = QSportEvent.sportEvent;

        if (sportEventFilter.startDate() != null) {
            builder.and(sportEvent.eventDateTime.after(sportEventFilter.startDate()));
        }

        if (sportEventFilter.endDate() != null) {
            builder.and(sportEvent.eventDateTime.before(sportEventFilter.endDate()));
        }

        if (sportEventFilter.arenaId() != null) {
            builder.and(sportEvent.arena.id.eq(sportEventFilter.arenaId()));
        }

        return builder;
    }

    /**
     * Finds a sporting event by its ID.
     *
     * @param id the ID of the sporting event
     * @return an {@link Optional} containing the found sporting event, or empty if not found
     */
    public Optional<SportEventReadDto> findById(Long id){
        return sportEventRepository.findById(id)
                .map(sportEventReadMapper::toDto);
    }

    /**
     * Creates a new sporting event.
     *
     * @param sportEventCreateEditDto the DTO of the sporting event to create
     */
    @Transactional
    public SportEventReadDto createSportEvent(SportEventCreateEditDto sportEventCreateEditDto,
                                              Long arenaId){
        try {
            SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sportEvent.setArena(arena);

            if (sportEventCreateEditDto.getImageFile() != null) {
                uploadImage(sportEventCreateEditDto.getImageFile());
            }

            SportEvent savedSportEvent = sportEventRepository.save(sportEvent);
            log.info("SportEvent created successfully with dto: {}", sportEventCreateEditDto);
            return sportEventReadMapper.toDto(savedSportEvent);
        } catch (DataAccessException e){
            log.error("Failed to create SportEvent with dto: {}", sportEventCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    /**
     * Updates an existing sporting event.
     *
     * @param id the ID of the sporting event to update
     * @param sportEventCreateEditDto the DTO of the updated sporting event
     */
    @Transactional
    public SportEventReadDto updateSportEvent(Long id,
                                              SportEventCreateEditDto sportEventCreateEditDto,
                                              Long arenaId) {
        try {
            SportEvent sportEventBeforeUpdate = sportEventRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Failed to find sport event with provided id: {}", id);
                        return new DaoResourceNotFoundException("Sport event not found");
                    });

            SportEvent sportEvent = sportEventCreateEditMapper.toEntity(sportEventCreateEditDto);
            Arena arena = arenaService.findArenaById(arenaId);
            sportEvent.setId(id);
            sportEvent.setArena(arena);

            updatePosterImageIfNecessary(sportEventCreateEditDto, sportEvent, sportEventBeforeUpdate);

            SportEvent updatedSportEvent = sportEventRepository.save(sportEvent);
            log.info("SportEvent with id {} updated successfully with dto: {}", id, sportEventCreateEditDto);
            return sportEventReadMapper.toDto(updatedSportEvent);
        } catch (DataAccessException e){
            log.error("Failed to update SportEvent {} with dto: {}", id, sportEventCreateEditDto);
            throw new DaoCrudException(e);
        }
    }

    private void updatePosterImageIfNecessary(SportEventCreateEditDto dto, SportEvent sportEvent, SportEvent sportEventBeforeUpdate) {
        if (dto.getImageFile() != null) {
            uploadImage(dto.getImageFile());
        } else {
            sportEvent.setPosterImage(sportEventBeforeUpdate.getPosterImage());
        }
    }

    /**
     * Deletes a sporting event by its ID.
     *
     * @param id the ID of the event to delete
     */
    @Transactional
    public void deleteSportEvent(Long id) {
        Optional<SportEvent> sportEventOptional = sportEventRepository.findById(id);
        if (sportEventOptional.isEmpty()) {
            log.error("Failed to find sport event with provided id: {}", id);
            throw new DaoResourceNotFoundException("Sport Event not found with id " + id);
        }
        try {
            sportEventRepository.deleteById(id);

            String imageUrl = sportEventOptional.get().getPosterImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (!sportEventRepository.existsByPosterImage(imageUrl)) {
                    deleteImage(imageUrl);
                }
            }

            log.info("SportEvent with id {} deleted successfully.", id);
        } catch (DataAccessException e){
            log.error("Failed to delete SportEvent with id {}", id);
            throw new DaoCrudException(e);
        }
    }

    public void uploadImage(MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    IMAGE_SERVICE_URL + "/upload", requestEntity, String.class
            );
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IOException("Failed to upload image, received status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while uploading image: ", e);
            throw e;
        } catch (ResourceAccessException e) {
            log.error("Resource access error occurred while uploading image: ", e);
            throw e;
        } catch (IOException e) {
            log.error("I/O error occurred while uploading image: ", e);
            throw new DaoCrudException(e);
        }
    }

    public byte[] fetchImageFromImageService(String fileName) {
        byte[] imageBytes = null;
        try {
            String url = IMAGE_SERVICE_URL + "/" + fileName;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody.containsKey("image")) {
                    String imageBase64 = (String) responseBody.get("image");
                    imageBytes = Base64.getDecoder().decode(imageBase64);
                }
            }
        } catch (RestClientException e) {
            log.error("Error fetching image from ImageService", e);
        }
        return imageBytes;
    }


    public void deleteImage(String filename) {
        restTemplate.delete(IMAGE_SERVICE_URL + "/" + filename);
    }

}
