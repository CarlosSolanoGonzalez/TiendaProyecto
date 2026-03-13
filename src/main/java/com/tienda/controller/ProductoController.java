package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tienda.domain.Categoria;
import com.tienda.domain.Producto;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "producto/lista";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerProducto(id));
        return "producto/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Producto producto = new Producto();
        producto.setCategoria(new Categoria());
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("accion", "/productos");
        return "producto/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerProducto(id));
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("accion", "/productos/" + id);
        return "producto/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Producto producto,
                          MultipartFile imagenFile,
                          RedirectAttributes redirectAttributes) {
        productoService.guardarProducto(producto, imagenFile);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto guardado correctamente");
        return "redirect:/productos";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Producto producto,
                             MultipartFile imagenFile,
                             RedirectAttributes redirectAttributes) {
        productoService.actualizarProducto(id, producto, imagenFile);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto actualizado correctamente");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.eliminarProducto(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado correctamente");
        return "redirect:/productos";
    }
}
