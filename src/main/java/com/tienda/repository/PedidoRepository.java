package com.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.domain.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
