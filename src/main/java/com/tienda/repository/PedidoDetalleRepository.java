package com.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.domain.PedidoDetalle;

public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
}
