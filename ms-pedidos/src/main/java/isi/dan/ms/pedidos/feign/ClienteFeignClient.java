package isi.dan.ms.pedidos.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "MS-CLIENTES")

public interface ClienteFeignClient {
    
    @GetMapping("/api/clientes/saldo")
    Boolean verificarSaldoCliente(@RequestParam("id") Integer id,  @RequestParam("montoTotal") BigDecimal montoTotal);
    
    @GetMapping("api/clientes/{id}/maximo-descubierto")
    BigDecimal obtenerMaximoDescubierto(@PathVariable Integer id);

        
}
