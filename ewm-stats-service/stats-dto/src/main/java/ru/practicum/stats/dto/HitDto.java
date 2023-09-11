package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    Long id; // id записи

    String app; // id сервиса для которого записывается информация

    String uri; // URI для которого был создан запрос

    String ip; // IP-адрес пользователя, который создал запрос

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp; //  Дата и время, когда был создан запрос к эндпоинту
}
