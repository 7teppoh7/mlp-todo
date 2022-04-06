package ru.sstu.mylittlediary.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDTO {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
