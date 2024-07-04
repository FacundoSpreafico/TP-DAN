package isi.dan.ms_usuarios.controllers;


import isi.dan.ms_usuarios.services.UserClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfilCliente")

public class UserClienteController {
    

    @Autowired
    private UserClienteService clienteService;


    

}
