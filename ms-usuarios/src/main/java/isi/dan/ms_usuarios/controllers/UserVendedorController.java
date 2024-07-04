package isi.dan.ms_usuarios.controllers;


import isi.dan.ms_usuarios.services.UserVendedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user/vendedor")


public class UserVendedorController {
    @Autowired
    private UserVendedorService vendedorService;


    @PostMapping
    void darAltaCliente() {
        
    }

}

