package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consulta")
@RequiredArgsConstructor
public class ConsultaController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/productos")
    public String consultarProductos(@RequestParam(required = false) String texto,
                                     @RequestParam(required = false) Long categoriaId,
                                     Model model) {
        if (texto != null && !texto.isBlank()) {
            model.addAttribute("productos", productoService.buscarPorNombre(texto));
        } else {
            model.addAttribute("productos", productoService.listarPorCategoria(categoriaId));
        }
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("texto", texto);
        model.addAttribute("categoriaId", categoriaId);
        return "producto/lista";
    }
}
