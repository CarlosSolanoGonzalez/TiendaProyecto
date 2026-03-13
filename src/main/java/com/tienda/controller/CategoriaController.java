package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tienda.domain.Categoria;
import com.tienda.service.CategoriaService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "categoria/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("accion", "/categorias");
        return "categoria/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.obtenerCategoria(id));
        model.addAttribute("accion", "/categorias/" + id);
        return "categoria/form";
    }

    @PostMapping
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes redirectAttributes) {
        categoriaService.guardarCategoria(categoria);
        redirectAttributes.addFlashAttribute("mensajeExito", "Categoria guardada correctamente");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @ModelAttribute Categoria categoria,
                             RedirectAttributes redirectAttributes) {
        categoriaService.actualizarCategoria(id, categoria);
        redirectAttributes.addFlashAttribute("mensajeExito", "Categoria actualizada correctamente");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminarCategoria(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Categoria eliminada correctamente");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/categorias";
    }
}
