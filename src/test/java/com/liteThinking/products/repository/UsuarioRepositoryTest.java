package com.liteThinking.products.repository;

import com.liteThinking.products.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void findByEmail_DeberiaRetornarUsuario() {
        Usuario usuario = new Usuario(null, "Juan", "Perez", "juan@test.com", "555", "pass", null);
        usuarioRepository.save(usuario);

        Optional<Usuario> result = usuarioRepository.findByEmail("juan@test.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("juan@test.com");
    }

    @Test
    void findByEmail_DeberiaRetornarEmpty_CuandoNoExiste() {
        Optional<Usuario> result = usuarioRepository.findByEmail("no@existe.com");
        assertThat(result).isEmpty();
    }
}
