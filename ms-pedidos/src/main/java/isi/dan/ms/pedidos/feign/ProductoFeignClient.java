package isi.dan.ms.pedidos.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "MS-PRODUCTOS")
public interface ProductoFeignClient {
    
    @PutMapping("/api/productos/{id}/stock")
    Boolean updateStockProducto(@PathVariable("id") Long id, @RequestParam ("cantidad") Integer cantidad);

    @GetMapping("/api/productos/{id}/precio")
    BigDecimal getPrecio(@PathVariable("id") Long id);

    @GetMapping("/api/productos/{id}/descuento")
    Integer getDescuento(@PathVariable("id") Long id);
}
