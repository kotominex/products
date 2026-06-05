package com.liteThinking.products.controller;

import com.liteThinking.products.dto.EmpresaRequest;
import com.liteThinking.products.dto.ProductoResponse;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.service.EmpresaService;
import com.liteThinking.products.service.PdfService;
import com.liteThinking.products.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;
    private final ProductoService productoService;
    private final PdfService pdfService;

    @Value("${app.tasa.cop-usd}")
    private double tasaUsd;

    @Value("${app.tasa.cop-eur}")
    private double tasaEur;

    @GetMapping
    public ResponseEntity<List<Empresa>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/{nit}")
    public ResponseEntity<Empresa> obtenerPorNit(@PathVariable String nit) {
        return ResponseEntity.ok(empresaService.obtenerPorNit(nit));
    }

    @PostMapping
    public ResponseEntity<Empresa> registrar(@Valid @RequestBody EmpresaRequest request) {
        Empresa empresa = empresaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(empresa);
    }

    @GetMapping("/{nit}/productos")
    public ResponseEntity<List<ProductoResponse>> listarProductos(@PathVariable String nit) {
        List<ProductoResponse> response = productoService.listarPorEmpresa(nit)
                .stream()
                .map(p -> ProductoResponse.fromEntity(p, tasaUsd, tasaEur))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{nit}/productos/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable String nit) {
        try {
            Empresa empresa = empresaService.obtenerPorNit(nit);
            List<com.liteThinking.products.entity.Producto> productos = productoService.listarPorEmpresa(nit);

            byte[] pdf = pdfService.generarInventario(empresa, productos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "inventario-" + nit + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
