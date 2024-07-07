package isi.dan.ms_usuarios.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import isi.dan.ms_usuarios.services.RolService;
@RestController
@RequestMapping("/api/roles")

public class RolController {
    @Autowired
    private RolService rolService;

    


}

