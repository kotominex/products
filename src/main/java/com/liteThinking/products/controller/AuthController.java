package com.liteThinking.products.controller;

import com.liteThinking.products.dto.LoginRequest;
import com.liteThinking.products.dto.RegistroRequest;
import com.liteThinking.products.dto.TokenResponse;
import com.liteThinking.products.entity.Rol;
import com.liteThinking.products.entity.Usuario;
import com.liteThinking.products.repository.RolRepository;
import com.liteThinking.products.repository.UsuarioRepository;
import com.liteThinking.products.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/registro")
    public ResponseEntity<TokenResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + request.rolId()));

        Usuario usuario = new Usuario();
        usuario.setNombres(request.nombres());
        usuario.setApellidos(request.apellidos());
        usuario.setEmail(request.email());
        usuario.setTelefono(request.telefono());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TokenResponse(token, "Bearer", usuario.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        String token = jwtUtil.generateToken(request.email());
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", request.email()));
    }
}
