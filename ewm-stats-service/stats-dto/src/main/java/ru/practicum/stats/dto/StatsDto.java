package ru.practicum.stats.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class StatsDto {

    private String app; // Название сервиса

    private String uri; // URI сервиса

    private Long hits; //  Количество просмотров
}

