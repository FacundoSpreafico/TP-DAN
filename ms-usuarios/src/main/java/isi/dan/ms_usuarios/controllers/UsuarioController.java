package isi.dan.ms_usuarios.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import isi.dan.ms_usuarios.services.UsuarioService;
import isi.dan.ms_usuarios.models.Usuario;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @GetMapping("/{username}")
    public Usuario getUsuarioByUsername(@PathVariable String username) {
        return usuarioService.getUsuarioByUsername(username);
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.createUsuario(usuario));
    }
    
}
