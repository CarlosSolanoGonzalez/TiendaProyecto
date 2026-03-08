package com.tienda.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.domain.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
