package com.liteThinking.products.controller;

import com.liteThinking.products.dto.ProductoRequest;
import com.liteThinking.products.entity.Producto;
import com.liteThinking.products.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> registrar(@Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }
}
