package ru.practicum.ewm.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.categories.CategoryDto;
import ru.practicum.ewm.dto.locations.LocationDto;
import ru.practicum.ewm.dto.users.UserShortDto;
import ru.practicum.ewm.model.enums.EventState;

import java.time.LocalDateTime;
import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    @JsonFormat(pattern = FOR_FORMATTER)
    LocalDateTime createdOn;

    String description;

    @JsonFormat(pattern = FOR_FORMATTER)
    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    LocationDto location;

    boolean paid;

    Integer participantLimit;

    @JsonFormat(pattern = FOR_FORMATTER)
    LocalDateTime publishedOn;

    Boolean requestModeration;

    EventState state;

    String title;

    Long views;
}