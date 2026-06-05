package com.liteThinking.products.controller;

import com.liteThinking.products.dto.ProductoRequest;
import com.liteThinking.products.entity.Producto;
import com.liteThinking.products.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private ProductoService productoService;

    @BeforeEach
    void setUp() {
        ProductoController controller = new ProductoController(productoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registrar_DeberiaRetornar201() throws Exception {
        ProductoRequest req = new ProductoRequest("Laptop", "16GB", BigDecimal.valueOf(3000), "123", 1L);
        when(productoService.crear(any())).thenAnswer(i -> {
            Producto p = new Producto();
            p.setProductoId(1L);
            p.setNombreProducto("Laptop");
            return p;
        });
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.productoId").value(1));
    }

    @Test
    void registrar_DeberiaRetornar400_CuandoDatosInvalidos() throws Exception {
        ProductoRequest req = new ProductoRequest(null, null, null, null, null);
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }
}
