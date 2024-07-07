package isi.dan.ms_usuarios.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "MS_USUARIOS_USUARIO")
@Data

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;
    private String username;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "MS_USUARIOS_ROL", 
               joinColumns = @JoinColumn(name = "usuario_id"), 
               inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles;
}
