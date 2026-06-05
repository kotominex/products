package com.liteThinking.products.repository;

import com.liteThinking.products.entity.Empresa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmpresaRepositoryTest {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Test
    void save_DeberiaPersistirYRecuperar() {
        Empresa empresa = new Empresa("123", "Test SA", "Calle 1", "555", "t@t.com");
        empresaRepository.save(empresa);

        Optional<Empresa> result = empresaRepository.findById("123");

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Test SA");
    }

    @Test
    void findById_DeberiaRetornarEmpty_CuandoNoExiste() {
        Optional<Empresa> result = empresaRepository.findById("inexistente");
        assertThat(result).isEmpty();
    }
}
