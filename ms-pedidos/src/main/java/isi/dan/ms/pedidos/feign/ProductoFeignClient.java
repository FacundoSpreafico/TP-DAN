package isi.dan.ms.pedidos.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MS-PRODUCTOS")
public interface ProductoFeignClient {
    
    @PutMapping("/api/productos/{id}/stock")
    Boolean updateStockProducto(@PathVariable("id") Long id, @RequestParam ("cantidad") Integer cantidad);

}
