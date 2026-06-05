package com.liteThinking.products.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "empresas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Empresa {

    @Id
    private String nit;

    @Column(nullable = false)
    private String nombre;

    private String direccion;
    private String telefono;
    private String email;
}
