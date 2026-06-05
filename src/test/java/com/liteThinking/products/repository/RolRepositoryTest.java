package com.liteThinking.products.repository;

import com.liteThinking.products.entity.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RolRepositoryTest {

    @Autowired
    private RolRepository rolRepository;

    @Test
    void findByNombre_DeberiaRetornarRol() {
        Rol rol = new Rol(null, "ADMIN");
        rolRepository.save(rol);

        Optional<Rol> result = rolRepository.findByNombre("ADMIN");

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("ADMIN");
    }
}
