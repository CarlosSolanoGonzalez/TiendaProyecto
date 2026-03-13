package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping({"", "/"})
    public String inicio(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "index";
    }
}
