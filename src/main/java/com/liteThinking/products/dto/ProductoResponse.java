package com.liteThinking.products.dto;

import com.liteThinking.products.entity.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record ProductoResponse(
    Long productoId,
    String nombreProducto,
    String caracteristicas,
    BigDecimal precioCop,
    BigDecimal precioUsd,
    BigDecimal precioEur,
    String categoria,
    String empresaNit
) {
    public static ProductoResponse fromEntity(Producto p, double tasaUsd, double tasaEur) {
        BigDecimal cop = p.getPrecio();
        BigDecimal usd = cop.multiply(BigDecimal.valueOf(tasaUsd)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal eur = cop.multiply(BigDecimal.valueOf(tasaEur)).setScale(2, RoundingMode.HALF_UP);
        return new ProductoResponse(
            p.getProductoId(),
            p.getNombreProducto(),
            p.getCaracteristicas(),
            cop,
            usd,
            eur,
            p.getCategoria() != null ? p.getCategoria().getNombreCategoria() : null,
            p.getEmpresa() != null ? p.getEmpresa().getNit() : null
        );
    }
}
