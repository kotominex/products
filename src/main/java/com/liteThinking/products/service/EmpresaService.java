package com.liteThinking.products.service;

import com.liteThinking.products.dto.EmpresaRequest;
import com.liteThinking.products.entity.Empresa;
import com.liteThinking.products.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public Empresa crear(EmpresaRequest request) {
        Empresa empresa = new Empresa();
        empresa.setNit(request.nit());
        empresa.setNombre(request.nombre());
        empresa.setDireccion(request.direccion());
        empresa.setTelefono(request.telefono());
        empresa.setEmail(request.email());
        return empresaRepository.save(empresa);
    }

    public Empresa obtenerPorNit(String nit) {
        return empresaRepository.findById(nit)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con nit: " + nit));
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }
}
