package com.example.ticketbookingsystem.mapper.sport_event_mapper;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.entity.SportEvent;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

/**
 * Mapper interface for converting between SportEvent entities and SportEventCreateEditDto.
 */
@Mapper(componentModel = "spring")
public interface SportEventCreateEditMapper {
    SportEventCreateEditMapper INSTANCE = Mappers.getMapper(SportEventCreateEditMapper.class);

    @Mapping(target = "posterImage", source = "imageFile", qualifiedByName = "fileToFilename")
    SportEvent toEntity(SportEventCreateEditDto sportEventCreateEditDto);

    SportEventCreateEditDto toDto(SportEvent sportEvent);

    @Mapping(target = "posterImage", ignore = true)
    void updateEntityFromDto(SportEventCreateEditDto sportEventCreateEditDto, @MappingTarget SportEvent sportEvent);

    @Named("fileToFilename")
    default String fileToImageName(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return file.getOriginalFilename();
        }
        return null;
    }

    @AfterMapping
    default void afterUpdateEntityFromDto(SportEventCreateEditDto sportEventCreateEditDto,
                                          @MappingTarget SportEvent sportEvent) {
        if (sportEventCreateEditDto.getImageFile() != null && !sportEventCreateEditDto.getImageFile().isEmpty()) {
            sportEvent.setPosterImage(sportEventCreateEditDto.getImageFile().getOriginalFilename());
        }
    }
}
