package com.liteThinking.products.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroRequest(
    @NotBlank String nombres,
    @NotBlank String apellidos,
    @Email String email,
    String telefono,
    @NotBlank String password,
    @NotNull Long rolId
) {}
