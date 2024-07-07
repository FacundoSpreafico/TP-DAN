package isi.dan.ms_usuarios.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "MS_USUARIOS_ROL")
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;
}
