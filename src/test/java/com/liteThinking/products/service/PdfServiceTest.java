package com.liteThinking.products.service;

import com.liteThinking.products.entity.Categoria;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.entity.Producto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfServiceTest {

    private final PdfService pdfService = new PdfService(0.00025, 0.00023);

    @Test
    void generarInventario_DeberiaRetornarBytesNoVacios() throws Exception {
        Empresa empresa = new Empresa("123", "Test SA", "Calle 1", "555", "test@test.com");
        Categoria cat = new Categoria(1L, "Electronica");
        Producto p1 = new Producto(1L, "Laptop", "16GB RAM", BigDecimal.valueOf(3000), empresa, cat);
        Producto p2 = new Producto(2L, "Mouse", "inalambrico", BigDecimal.valueOf(50), empresa, cat);

        byte[] pdf = pdfService.generarInventario(empresa, List.of(p1, p2));

        assertThat(pdf).isNotEmpty();
        assertThat(pdf[0]).isEqualTo((byte) 0x25);
    }
}