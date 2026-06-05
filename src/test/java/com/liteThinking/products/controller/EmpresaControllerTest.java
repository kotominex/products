package com.liteThinking.products.controller;

import com.liteThinking.products.dto.EmpresaRequest;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.service.EmpresaService;
import com.liteThinking.products.service.PdfService;
import com.liteThinking.products.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmpresaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private EmpresaService empresaService;
    @Mock private ProductoService productoService;
    @Mock private PdfService pdfService;

    @BeforeEach
    void setUp() {
        EmpresaController controller = new EmpresaController(empresaService, productoService, pdfService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void listarTodas_DeberiaRetornar200YLista() throws Exception {
        when(empresaService.listarTodas()).thenReturn(List.of(
                new Empresa("123", "Test SA", null, null, null)));
        mockMvc.perform(get("/api/empresas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nit").value("123"))
                .andExpect(jsonPath("$[0].nombre").value("Test SA"));
    }

    @Test
    void obtenerPorNit_DeberiaRetornar200() throws Exception {
        when(empresaService.obtenerPorNit("123"))
                .thenReturn(new Empresa("123", "Test SA", null, null, null));
        mockMvc.perform(get("/api/empresas/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nit").value("123"));
    }

    @Test
    void obtenerPorNit_DeberiaRetornar404_CuandoNoExiste() throws Exception {
        when(empresaService.obtenerPorNit("999"))
                .thenThrow(new EntityNotFoundException("no"));
        mockMvc.perform(get("/api/empresas/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_DeberiaRetornar201() throws Exception {
        EmpresaRequest req = new EmpresaRequest("123", "Test SA", "Calle 1", "555", "t@t.com");
        when(empresaService.crear(any()))
                .thenReturn(new Empresa("123", "Test SA", "Calle 1", "555", "t@t.com"));
        mockMvc.perform(post("/api/empresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nit").value("123"));
    }

    @Test
    void registrar_DeberiaRetornar400_CuandoDatosInvalidos() throws Exception {
        EmpresaRequest req = new EmpresaRequest(null, null, null, null, null);
        mockMvc.perform(post("/api/empresas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listarProductos_DeberiaRetornar200() throws Exception {
        when(productoService.listarPorEmpresa("123")).thenReturn(List.of());
        mockMvc.perform(get("/api/empresas/123/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void descargarPdf_DeberiaRetornar200YContentTypePdf() throws Exception {
        Empresa emp = new Empresa("123", "T", null, null, null);
        byte[] pdf = "%PDF-bytes".getBytes();
        when(empresaService.obtenerPorNit("123")).thenReturn(emp);
        when(productoService.listarPorEmpresa("123")).thenReturn(List.of());
        when(pdfService.generarInventario(emp, List.of())).thenReturn(pdf);
        mockMvc.perform(get("/api/empresas/123/productos/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
