package com.liteThinking.products.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmpresaRequest(
    @NotBlank String nit,
    @NotBlank String nombre,
    String direccion,
    String telefono,
    @Email String email
) {}
