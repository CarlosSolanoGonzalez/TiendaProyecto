package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tienda.domain.Carrito;
import com.tienda.service.CarritoService;
import com.tienda.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String verCarrito(Model model) {
        Carrito carrito = carritoService.obtenerOCrearCarrito(usuarioService.obtenerUsuarioAutenticado().getId());
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", carritoService.calcularTotal(carrito));
        return "carrito/ver";
    }

    @PostMapping("/agregar/{productoId}")
    public String agregarProducto(@PathVariable Long productoId,
                                  @RequestParam(defaultValue = "1") Integer cantidad,
                                  RedirectAttributes redirectAttributes) {
        try {
            carritoService.agregarProducto(usuarioService.obtenerUsuarioAutenticado().getId(), productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto agregado al carrito");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/item/{itemId}/actualizar")
    public String actualizarCantidad(@PathVariable Long itemId,
                                     @RequestParam Integer cantidad,
                                     RedirectAttributes redirectAttributes) {
        try {
            carritoService.actualizarCantidad(usuarioService.obtenerUsuarioAutenticado().getId(), itemId, cantidad);
            redirectAttributes.addFlashAttribute("mensajeExito", "Carrito actualizado");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/item/{itemId}/eliminar")
    public String eliminarItem(@PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            carritoService.eliminarItem(usuarioService.obtenerUsuarioAutenticado().getId(), itemId);
            redirectAttributes.addFlashAttribute("mensajeExito", "Item eliminado del carrito");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/comprar")
    public String comprar(RedirectAttributes redirectAttributes) {
        try {
            carritoService.procesarCompra(usuarioService.obtenerUsuarioAutenticado().getId());
            redirectAttributes.addFlashAttribute("mensajeExito", "Gracias por su compra");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/carrito";
    }
}
