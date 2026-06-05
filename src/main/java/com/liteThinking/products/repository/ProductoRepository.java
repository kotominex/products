package com.liteThinking.products.repository;

import com.liteThinking.products.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByEmpresa_Nit(String nit);
}
