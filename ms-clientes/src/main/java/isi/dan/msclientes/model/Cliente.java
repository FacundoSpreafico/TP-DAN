package isi.dan.msclientes.model;

import java.math.BigDecimal;
import java.util.HashSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Table(name = "MS_CLI_CLIENTE")
@Data
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="NOMBRE")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(name="CORREO_ELECTRONICO")
    @Email(message = "Email debe ser valido")
    @NotBlank(message = "Email es obligatorio")
    private String correoElectronico;
    
    @Column(name="CUIT")
    private String cuit;

    @Value("${maximo.descubierto}")
    @Column(name="MAXIMO_DESCUBIERTO")
    @Min(value = 10000, message = "El descubierto maximo debe ser al menos 10000")
    private BigDecimal maximoDescubierto;
    
    @Column (name = "MAXIMA_CANTIDAD_OBRAS")
    private Integer maximaCantidadObras;

    @OneToMany
    private Set<UsuarioHabilitado> usuariosHabilitados = new HashSet<>();

    public void addUsuarioHabilitado(UsuarioHabilitado usuarioHabilitado) {
        usuarioHabilitado.setCliente(this);
        this.usuariosHabilitados.add(usuarioHabilitado);
     }
  
     public void removeUsuarioHabilitado(UsuarioHabilitado usuarioHabilitado) {
        usuarioHabilitado.setCliente(null);
        this.usuariosHabilitados.remove(usuarioHabilitado);
     }
}
