package ru.practicum.ewm.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.dto.locations.LocationDto;
import ru.practicum.ewm.model.enums.StateAction;
import ru.practicum.ewm.validation.EventDateValidator;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventDto {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = FOR_FORMATTER)
    @EventDateValidator
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}