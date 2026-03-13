package com.tienda.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.domain.Carrito;
import com.tienda.domain.CarritoItem;
import com.tienda.domain.Producto;
import com.tienda.domain.Usuario;
import com.tienda.repository.CarritoItemRepository;
import com.tienda.repository.CarritoRepository;
import com.tienda.repository.ProductoRepository;
import com.tienda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearCarrito(usuarioId));
    }

    public Carrito agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        int cantidadFinal = cantidad == null || cantidad < 1 ? 1 : cantidad;

        CarritoItem item = carrito.getItems().stream()
                .filter(carritoItem -> carritoItem.getProducto() != null
                && productoId.equals(carritoItem.getProducto().getId()))
                .findFirst()
                .or(() -> carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId))
                .orElseGet(() -> CarritoItem.builder()
                        .carrito(carrito)
                        .producto(producto)
                        .cantidad(0)
                        .precio(producto.getPrecio())
                        .build());

        int nuevaCantidad = item.getCantidad() + cantidadFinal;
        validarExistencias(producto, nuevaCantidad);

        item.setCantidad(nuevaCantidad);
        item.setPrecio(producto.getPrecio());
        CarritoItem itemGuardado = carritoItemRepository.save(item);
        if (carrito.getItems().stream().noneMatch(carritoItem -> carritoItem.getId() != null
                && carritoItem.getId().equals(itemGuardado.getId()))) {
            carrito.getItems().add(itemGuardado);
        }
        return carritoRepository.findById(carrito.getId()).orElse(carrito);
    }

    public Carrito actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado: " + itemId));

        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new IllegalArgumentException("El item no pertenece al carrito del usuario");
        }

        if (cantidad == null || cantidad <= 0) {
            carrito.getItems().removeIf(carritoItem -> carritoItem.getId().equals(item.getId()));
            carritoItemRepository.delete(item);
        } else {
            validarExistencias(item.getProducto(), cantidad);
            item.setCantidad(cantidad);
            carritoItemRepository.save(item);
        }

        return carritoRepository.findById(carrito.getId()).orElse(carrito);
    }

    public void eliminarItem(Long usuarioId, Long itemId) {
        actualizarCantidad(usuarioId, itemId, 0);
    }

    public void limpiarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    public void procesarCompra(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        if (carrito.getItems().isEmpty()) {
            throw new IllegalStateException("El carrito esta vacio");
        }

        for (CarritoItem item : carrito.getItems()) {
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProducto().getId()));
            validarExistencias(producto, item.getCantidad());
        }

        for (CarritoItem item : carrito.getItems()) {
            Producto producto = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProducto().getId()));
            producto.setExistencias(producto.getExistencias() - item.getCantidad());
            productoRepository.save(producto);
        }

        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(Carrito carrito) {
        return carrito.getItems().stream()
                .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Carrito crearCarrito(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));
        return carritoRepository.save(Carrito.builder()
                .usuario(usuario)
                .fechaCreacion(LocalDateTime.now())
                .build());
    }

    private void validarExistencias(Producto producto, Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        if (producto.getExistencias() == null || producto.getExistencias() < 0) {
            throw new IllegalStateException("El producto " + producto.getNombre() + " no tiene stock valido");
        }

        if (cantidad > producto.getExistencias()) {
            throw new IllegalStateException("Stock insuficiente para " + producto.getNombre()
                    + ". Disponible: " + producto.getExistencias());
        }
    }
}
