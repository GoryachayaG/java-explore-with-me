package ru.practicum.ewm.dto.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @Email
    @Length(min = 6, max = 254)
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 2, max = 250)
    private String name;
}