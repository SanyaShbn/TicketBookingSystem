package com.example.ticketbookingsystem.mapper.sport_event_mapper;

import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventCreateEditDto;
import com.example.ticketbookingsystem.entity.SportEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

/**
 * Mapper interface for converting between SportEvent entities and SportEventCreateEditDto.
 */
@Mapper(componentModel = "spring")
public interface SportEventCreateEditMapper {
    SportEventCreateEditMapper INSTANCE = Mappers.getMapper(SportEventCreateEditMapper.class);

    @Mapping(target = "posterImageUrl", source = "imageFile", qualifiedByName = "fileToUrl")
    SportEvent toEntity(SportEventCreateEditDto sportEventCreateEditDto);

    SportEventCreateEditDto toDto(SportEvent sportEvent);

    @Named("fileToUrl")
    default String fileToUrl(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return file.getOriginalFilename();
        }
        return null;
    }
}
