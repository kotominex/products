package com.liteThinking.products.service;

import com.liteThinking.products.dto.ProductoRequest;
import com.liteThinking.products.entity.Categoria;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.entity.Producto;
import com.liteThinking.products.repository.CategoriaRepository;
import com.liteThinking.products.repository.EmpresaRepository;
import com.liteThinking.products.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final EmpresaRepository empresaRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Producto crear(ProductoRequest request) {
        Empresa empresa = empresaRepository.findById(request.empresaNit())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con nit: " + request.empresaNit()));

        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada con id: " + request.idCategoria()));

        Producto producto = new Producto();
        producto.setNombreProducto(request.nombreProducto());
        producto.setCaracteristicas(request.caracteristicas());
        producto.setPrecio(request.precio());
        producto.setEmpresa(empresa);
        producto.setCategoria(categoria);

        return productoRepository.save(producto);
    }

    public List<Producto> listarPorEmpresa(String nit) {
        return productoRepository.findByEmpresa_Nit(nit);
    }
}
