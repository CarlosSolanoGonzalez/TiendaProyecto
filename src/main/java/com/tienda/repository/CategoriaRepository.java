package com.tienda.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tienda.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
