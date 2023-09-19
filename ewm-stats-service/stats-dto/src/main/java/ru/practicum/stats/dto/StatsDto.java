package ru.practicum.stats.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsDto {

    String app; // Название сервиса

    String uri; // URI сервиса

    Long hits; //  Количество просмотров
}

