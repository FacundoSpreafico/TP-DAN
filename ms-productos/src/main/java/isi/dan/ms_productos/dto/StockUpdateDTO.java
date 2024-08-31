package isi.dan.ms_productos.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockUpdateDTO {
    private Long idProducto;
    private Integer cantidad;
    private BigDecimal precio;
}
