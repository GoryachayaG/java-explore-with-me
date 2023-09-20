package ru.practicum.ewm.dto.users;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    @Email
    @Length(min = 6, max = 254)
    @NotBlank
    String email;

    @NotBlank
    @Length(min = 2, max = 250)
    String name;
}