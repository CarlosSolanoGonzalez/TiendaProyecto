package com.tienda.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.domain.CarritoItem;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    Optional<CarritoItem> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
}
