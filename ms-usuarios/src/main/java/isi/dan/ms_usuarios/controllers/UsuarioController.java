package isi.dan.ms_usuarios.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import isi.dan.ms_usuarios.services.UsuarioService;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;


    
}
