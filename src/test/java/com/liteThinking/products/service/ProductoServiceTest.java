package com.liteThinking.products.service;

import com.liteThinking.products.dto.ProductoRequest;
import com.liteThinking.products.entity.Categoria;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.entity.Producto;
import com.liteThinking.products.repository.CategoriaRepository;
import com.liteThinking.products.repository.EmpresaRepository;
import com.liteThinking.products.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crear_DeberiaGuardarYRetornarProducto() {
        Empresa empresa = new Empresa("123", "Test SA", null, null, null);
        Categoria categoria = new Categoria(1L, "Electronica");
        ProductoRequest request = new ProductoRequest("Laptop", "16GB RAM", BigDecimal.valueOf(3000), "123", 1L);

        when(empresaRepository.findById("123")).thenReturn(Optional.of(empresa));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = productoService.crear(request);

        assertThat(resultado.getNombreProducto()).isEqualTo("Laptop");
        assertThat(resultado.getEmpresa()).isEqualTo(empresa);
        assertThat(resultado.getCategoria()).isEqualTo(categoria);
        verify(productoRepository).save(any());
    }

    @Test
    void crear_DeberiaLanzarExcepcion_CuandoEmpresaNoExiste() {
        ProductoRequest request = new ProductoRequest("Laptop", null, BigDecimal.TEN, "999", 1L);
        when(empresaRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crear_DeberiaLanzarExcepcion_CuandoCategoriaNoExiste() {
        Empresa empresa = new Empresa("123", "Test SA", null, null, null);
        ProductoRequest request = new ProductoRequest("Laptop", null, BigDecimal.TEN, "123", 999L);
        when(empresaRepository.findById("123")).thenReturn(Optional.of(empresa));
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
        verify(productoRepository, never()).save(any());
    }

    @Test
    void listarPorEmpresa_DeberiaRetornarLista() {
        when(productoRepository.findByEmpresa_Nit("123")).thenReturn(List.of(new Producto()));

        List<Producto> resultado = productoService.listarPorEmpresa("123");

        assertThat(resultado).hasSize(1);
    }
}