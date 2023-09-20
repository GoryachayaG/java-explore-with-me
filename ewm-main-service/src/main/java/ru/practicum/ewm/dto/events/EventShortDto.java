package ru.practicum.ewm.dto.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.categories.CategoryDto;
import ru.practicum.ewm.dto.users.UserShortDto;

import java.time.LocalDateTime;
import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    @JsonFormat(pattern = FOR_FORMATTER)
    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    boolean paid;

    String title;

    Long views;
}