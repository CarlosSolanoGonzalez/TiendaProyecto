package com.tienda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.domain.Categoria;
import com.tienda.repository.CategoriaRepository;
import com.tienda.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + id));
    }

    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoria) {
        Categoria actual = obtenerCategoria(id);
        actual.setNombre(categoria.getNombre());
        actual.setDescripcion(categoria.getDescripcion());
        return categoriaRepository.save(actual);
    }

    public void eliminarCategoria(Long id) {
        if (productoRepository.existsByCategoriaId(id)) {
            throw new IllegalStateException("No se puede eliminar la categoria porque tiene productos asociados");
        }
        categoriaRepository.deleteById(id);
    }
}
