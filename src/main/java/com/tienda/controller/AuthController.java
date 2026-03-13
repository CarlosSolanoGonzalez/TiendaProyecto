package com.tienda.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tienda.domain.Usuario;
import com.tienda.service.AuthService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("mensajeError", "Correo o contrasena incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensajeExito", "Sesion cerrada correctamente");
        }
        return "usuario/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute Usuario usuario,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "usuario/registro";
        }

        try {
            authService.registrarCliente(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta creada correctamente");
            return "redirect:/usuarios/login";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("mensajeError", ex.getMessage());
            return "usuario/registro";
        }
    }
}
