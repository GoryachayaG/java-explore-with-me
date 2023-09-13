package ru.practicum.ewm.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = FOR_FORMATTER)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = FOR_FORMATTER)
    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = FOR_FORMATTER)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}