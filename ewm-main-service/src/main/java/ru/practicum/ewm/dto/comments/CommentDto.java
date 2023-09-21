package ru.practicum.ewm.dto.comments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.users.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.stats.dto.Constants.FOR_FORMATTER;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    Long id;

    String text;

    UserShortDto author;

    @JsonFormat(pattern = FOR_FORMATTER)
    LocalDateTime publishedOn;

    Long eventId;
}
