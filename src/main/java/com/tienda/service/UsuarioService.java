package com.tienda.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.tienda.domain.Rol;
import com.tienda.domain.Usuario;
import com.tienda.repository.RolRepository;
import com.tienda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + correo));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreoSegunLogin(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerClientePorDefecto() {
        return usuarioRepository.findByCorreo("cliente@tienda.com")
                .orElseGet(() -> usuarioRepository.findByActivoTrue().stream()
                        .filter(usuario -> usuario.getRol() != null
                                && ("CLIENTE".equalsIgnoreCase(usuario.getRol().getNombre())
                                || "CLIENT".equalsIgnoreCase(usuario.getRol().getNombre())))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No existe un usuario CLIENTE disponible")));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalStateException("No hay un usuario autenticado");
        }
        return obtenerPorCorreo(authentication.getName());
    }

    public Usuario registrarCliente(Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .or(() -> rolRepository.findByNombre("CLIENT"))
                .orElseThrow(() -> new IllegalStateException("No existe el rol CLIENTE en la base de datos"));
        usuario.setRol(rolCliente);
        usuario.setActivo(Boolean.TRUE);
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }
}
