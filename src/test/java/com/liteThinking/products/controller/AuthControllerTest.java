package com.liteThinking.products.controller;

import com.liteThinking.products.dto.LoginRequest;
import com.liteThinking.products.dto.RegistroRequest;
import com.liteThinking.products.entity.Rol;
import com.liteThinking.products.entity.Usuario;
import com.liteThinking.products.repository.RolRepository;
import com.liteThinking.products.repository.UsuarioRepository;
import com.liteThinking.products.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(usuarioRepository, rolRepository,
                passwordEncoder, jwtUtil, authenticationManager);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registrar_DeberiaRetornar201() throws Exception {
        RegistroRequest req = new RegistroRequest("Juan", "Perez", "juan@test.com", "555", "pass123", 1L);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(rolRepository.findById(1L)).thenReturn(Optional.of(new Rol(1L, "ADMIN")));
        when(passwordEncoder.encode(anyString())).thenReturn("enc");
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt");

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("jwt"))
            .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void registrar_DeberiaRetornar409_CuandoEmailDuplicado() throws Exception {
        RegistroRequest req = new RegistroRequest("J", "P", "dup@test.com", "5", "p", 1L);
        when(usuarioRepository.findByEmail(req.email())).thenReturn(Optional.of(new Usuario()));

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict());
    }

    @Test
    void registrar_DeberiaRetornar400_CuandoRolNoExiste() throws Exception {
        RegistroRequest req = new RegistroRequest("J", "P", "j@t.com", "5", "p", 999L);
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(rolRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_DeberiaRetornar400_CuandoDatosInvalidos() throws Exception {
        RegistroRequest req = new RegistroRequest(null, null, null, null, null, null);
        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_DeberiaRetornar200() throws Exception {
        LoginRequest req = new LoginRequest("u@t.com", "p");
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(jwtUtil.generateToken(req.email())).thenReturn("jwt");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt"))
            .andExpect(jsonPath("$.email").value("u@t.com"));
    }

    @Test
    void login_DeberiaRetornar401_CuandoCredencialesInvalidas() throws Exception {
        LoginRequest req = new LoginRequest("u@t.com", "wrong");
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized());
    }
}
