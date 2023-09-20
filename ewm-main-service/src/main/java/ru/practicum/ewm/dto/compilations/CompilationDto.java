package ru.practicum.ewm.dto.compilations;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.events.EventShortDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    List<EventShortDto> events;

    Long id;

    Boolean pinned;

    String title;
}