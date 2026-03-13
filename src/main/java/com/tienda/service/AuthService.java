package com.tienda.service;

import org.springframework.stereotype.Service;
import com.tienda.domain.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioService usuarioService;

    public Usuario registrarCliente(Usuario usuario) {
        return usuarioService.registrarCliente(usuario);
    }
}
