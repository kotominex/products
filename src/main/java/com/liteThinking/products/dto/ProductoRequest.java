package com.liteThinking.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductoRequest(
    @NotBlank String nombreProducto,
    String caracteristicas,
    @NotNull @Positive BigDecimal precio,
    @NotBlank String empresaNit,
    @NotNull Long idCategoria
) {}
