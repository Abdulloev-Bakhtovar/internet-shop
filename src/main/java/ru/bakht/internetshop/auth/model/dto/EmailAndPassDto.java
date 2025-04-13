package ru.bakht.internetshop.auth.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailAndPassDto {

    String email;
    String password;
    String captchaToken;
}
