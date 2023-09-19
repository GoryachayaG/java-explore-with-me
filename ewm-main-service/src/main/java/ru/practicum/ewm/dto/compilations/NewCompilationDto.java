package ru.practicum.ewm.dto.compilations;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    Set<Long> events;

    Boolean pinned;

    @Length(min = 1, max = 50, groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    String title;
}