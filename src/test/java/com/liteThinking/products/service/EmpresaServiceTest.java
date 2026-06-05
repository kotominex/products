package com.liteThinking.products.service;

import com.liteThinking.products.dto.EmpresaRequest;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    @Test
    void crear_DeberiaGuardarYRetornarEmpresa() {
        EmpresaRequest request = new EmpresaRequest("123", "Test SA", "Calle 1", "555", "test@test.com");
        Empresa empresa = new Empresa("123", "Test SA", "Calle 1", "555", "test@test.com");

        when(empresaRepository.save(any())).thenReturn(empresa);

        Empresa resultado = empresaService.crear(request);

        assertThat(resultado.getNit()).isEqualTo("123");
        assertThat(resultado.getNombre()).isEqualTo("Test SA");
        verify(empresaRepository).save(any());
    }

    @Test
    void obtenerPorNit_DeberiaRetornarEmpresa_CuandoExiste() {
        Empresa empresa = new Empresa("123", "Test SA", null, null, null);
        when(empresaRepository.findById("123")).thenReturn(Optional.of(empresa));

        Empresa resultado = empresaService.obtenerPorNit("123");

        assertThat(resultado).isEqualTo(empresa);
    }

    @Test
    void obtenerPorNit_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(empresaRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> empresaService.obtenerPorNit("999"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void listarTodas_DeberiaRetornarLista() {
        when(empresaRepository.findAll()).thenReturn(List.of(
                new Empresa("1", "A", null, null, null),
                new Empresa("2", "B", null, null, null)
        ));

        List<Empresa> resultado = empresaService.listarTodas();

        assertThat(resultado).hasSize(2);
    }
}
