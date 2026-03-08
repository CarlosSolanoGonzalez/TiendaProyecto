package com.tienda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tienda.domain.Categoria;
import com.tienda.domain.Producto;
import com.tienda.repository.CategoriaRepository;
import com.tienda.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return categoriaId == null ? listarProductos() : productoRepository.findByCategoriaId(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return (nombre == null || nombre.isBlank())
                ? listarProductos()
                : productoRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    public Producto guardarProducto(Producto producto, MultipartFile imagenFile) {
        producto.setCategoria(resolverCategoria(producto));
        String imagenUrl = firebaseStorageService.cargaImagen(imagenFile, "productos");
        if (imagenUrl != null) {
            producto.setImagenUrl(imagenUrl);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto producto, MultipartFile imagenFile) {
        Producto actual = obtenerProducto(id);
        actual.setNombre(producto.getNombre());
        actual.setDescripcion(producto.getDescripcion());
        actual.setPrecio(producto.getPrecio());
        actual.setExistencias(producto.getExistencias());
        actual.setCategoria(resolverCategoria(producto));

        String imagenUrl = firebaseStorageService.cargaImagen(imagenFile, "productos");
        if (imagenUrl != null) {
            actual.setImagenUrl(imagenUrl);
        }
        return productoRepository.save(actual);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    private Categoria resolverCategoria(Producto producto) {
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("La categoria es obligatoria");
        }
        return categoriaRepository.findById(producto.getCategoria().getId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + producto.getCategoria().getId()));
    }
}
