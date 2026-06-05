package com.liteThinking.products.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ordenes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordenId;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "codigo_producto")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;
}
