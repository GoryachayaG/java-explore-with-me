package ru.practicum.ewm.dto.compilations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    @Length(min = 1, max = 50, groups = {Create.class, Update.class})
    @NotBlank(groups = Create.class)
    private String title;
}