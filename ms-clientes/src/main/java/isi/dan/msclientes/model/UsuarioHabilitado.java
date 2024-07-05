package isi.dan.msclientes.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;



@Entity
@Table (name = "MS_USUARIOS_HABILITADOS")
@Data
public class UsuarioHabilitado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="NOMBRE")
    private String nombre;
    @Column(name="APELLIDO")
    private String apellido;
    @Column(name="CORREO_ELECTRONICO")
    @Email(message = "Email debe ser valido")
    private String correoElectronico;
    @Column(name="DNI")
    private String dni;

    @ManyToOne
    @JoinColumn(name = "ID_CLIENTE")
    private Cliente cliente;



}
