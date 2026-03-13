package com.tienda.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.domain.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByCategoriaId(Long categoriaId);
}
