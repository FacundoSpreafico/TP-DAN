package isi.dan.ms_productos.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "MS_PRD_PRODUCTO")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;
    private String descripcion;
    @Column(name ="STOCK_ACTUAL")
    private int stockActual;
    @Column(name ="STOCK_MINIMO")
    private int stockMinimo;
    private BigDecimal precio;
    @ManyToOne
    @JoinColumn(name = "ID_CATEGORIA")
    private Categoria categoria;
    private Integer descuentoPromocional;


}
