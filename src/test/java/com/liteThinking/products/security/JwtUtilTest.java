package com.liteThinking.products.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    private static final long EXPIRATION = 3600000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION);
    }

    @Test
    void generateToken_DeberiaGenerarTokenNoVacio() {
        String token = jwtUtil.generateToken("test@test.com");
        assertThat(token).isNotBlank();
    }

    @Test
    void extractEmail_DeberiaRetornarEmail() {
        String token = jwtUtil.generateToken("user@example.com");
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void isTokenValid_DeberiaRetornarTrue_ParaTokenValido() {
        String token = jwtUtil.generateToken("user@example.com");
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_DeberiaRetornarFalse_CuandoTokenInvalido() {
        assertThat(jwtUtil.isTokenValid("token-invalido")).isFalse();
    }

    @Test
    void isTokenValid_DeberiaRetornarFalse_CuandoTokenExpirado() {
        JwtUtil expirado = new JwtUtil(SECRET, -1);
        String token = expirado.generateToken("user@example.com");
        assertThat(expirado.isTokenValid(token)).isFalse();
    }
}