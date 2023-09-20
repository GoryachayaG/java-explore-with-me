package ru.practicum.ewm.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.dto.locations.LocationDto;
import ru.practicum.ewm.validation.EventDateValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @Length(min = 20, max = 7000)
    String description;

    @NotNull
    @JsonFormat(pattern = FOR_FORMATTER)
    @EventDateValidator
    LocalDateTime eventDate;

    @NotNull
    LocationDto location;

    boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    @NotBlank
    @Length(min = 3, max = 120)
    String title;
}