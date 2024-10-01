package isi.dan.ms.pedidos.modelo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@Document(collection = "pedidos")
@Data
public class Pedido {
    public static final String SEQUENCE_NAME = "pedidos_sequence";
    @Id
    private String id;
    private Instant fecha;
    private Integer numeroPedido;
    private String usuario;
    private String observaciones;

    private Cliente cliente;
    private BigDecimal total;
    private Estado estado;
    @Field("detalle")
    private List<DetallePedido> detalle;

}

