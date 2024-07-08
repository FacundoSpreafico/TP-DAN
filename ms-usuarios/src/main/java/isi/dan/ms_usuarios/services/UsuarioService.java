package isi.dan.ms_usuarios.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import isi.dan.ms_usuarios.dao.UsuarioRepository;
import java.util.List;
import isi.dan.ms_usuarios.models.Usuario;

@Service

public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
