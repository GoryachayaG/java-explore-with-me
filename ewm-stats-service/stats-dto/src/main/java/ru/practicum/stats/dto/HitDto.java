package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HitDto {
    Long id; // id записи

    @NotBlank
    String app; // id сервиса для которого записывается информация

    @NotBlank
    String uri; // URI для которого был создан запрос

    @NotBlank
    String ip; // IP-адрес пользователя, который создал запрос

    @JsonFormat(pattern = FOR_FORMATTER)
    LocalDateTime timestamp; //  Дата и время, когда был создан запрос к эндпоинту
}
