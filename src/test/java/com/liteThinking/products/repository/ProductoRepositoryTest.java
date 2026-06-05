package com.liteThinking.products.repository;

import com.liteThinking.products.entity.Categoria;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.entity.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    private Empresa empresa;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        empresa = empresaRepository.save(new Empresa("123", "Test SA", null, null, null));
        categoria = categoriaRepository.save(new Categoria(null, "Electronica"));
    }

    @Test
    void findByEmpresa_Nit_DeberiaRetornarProductos() {
        Producto p1 = new Producto(null, "Laptop", "16GB", BigDecimal.valueOf(3000), empresa, categoria);
        Producto p2 = new Producto(null, "Mouse", null, BigDecimal.valueOf(50), empresa, categoria);
        productoRepository.save(p1);
        productoRepository.save(p2);

        List<Producto> result = productoRepository.findByEmpresa_Nit("123");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Producto::getNombreProducto)
                .containsExactlyInAnyOrder("Laptop", "Mouse");
    }

    @Test
    void findByEmpresa_Nit_DeberiaRetornarVacio_CuandoNoHayProductos() {
        List<Producto> result = productoRepository.findByEmpresa_Nit("inexistente");
        assertThat(result).isEmpty();
    }
}
