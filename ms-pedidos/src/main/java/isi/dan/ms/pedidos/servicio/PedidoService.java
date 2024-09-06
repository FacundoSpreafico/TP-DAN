package isi.dan.ms.pedidos.servicio;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import isi.dan.ms.pedidos.conf.RabbitMQConfig;
import isi.dan.ms.pedidos.dao.PedidoRepository;
import isi.dan.ms.pedidos.modelo.DetallePedido;
import isi.dan.ms.pedidos.modelo.Estado;
import isi.dan.ms.pedidos.modelo.Pedido;
import isi.dan.ms.pedidos.modelo.Producto;
import isi.dan.ms.pedidos.feign.ClienteFeignClient;
import isi.dan.ms.pedidos.feign.ProductoFeignClient;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ClienteFeignClient clienteFeign;

    @Autowired
    private ProductoFeignClient productoFeign; 

    Logger log = LoggerFactory.getLogger(PedidoService.class);

    public Pedido savePedido(Pedido pedido) {
    BigDecimal total = BigDecimal.ZERO;
    boolean stockSuficiente = true;
   
    //FALTA AGREGAR LA LOGICA PARA AÑADIR EL NUMERO DE PEDIDO
    pedido.setFecha(ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toInstant());

    for (DetallePedido dp : pedido.getDetalle()) {
        dp.setPrecioFinal(dp.getPrecioUnitario().multiply(BigDecimal.valueOf(dp.getCantidad())));
        total = total.add(dp.getPrecioFinal());
    }

    //VERIFICAR SALDO DEL CLIENTE
    if (!clienteFeign.verificarSaldoCliente(pedido.getCliente().getId(), total)){
        pedido.setTotal(total);
        pedido.setEstado(Estado.RECHAZADO);
        log.info("Pedido rechazado. El cliente no tiene saldo. Enviado desde savePedido (PedidoService)", total);
        return pedidoRepository.save(pedido);
    }

    for (DetallePedido dp : pedido.getDetalle()) {
        log.info("Verificando y actualizando stock para el producto {}", dp.getProducto().getId());
        Boolean stockActualizado = productoFeign.updateStockProducto(dp.getProducto().getId(), dp.getCantidad());

        if (!stockActualizado) {
            stockSuficiente = false;  // Si algún producto no tiene stock, lo marcamos
            break;
        }
    }

        // SI HAY STOCK SUFICIENTE DE TODOS LOS PRODUCTOS, SE CAMBIA EL ESTADO A EN PREPARACION
        if (stockSuficiente){
            pedido.setEstado(Estado.EN_PREPARACION);
            log.info("Pedido en preparación. Enviado desde savePedido (PedidoService)", total);
        }
        // SI NO HAY STOCK SUFICIENTE DE ALGUN PRODUCTO, SE CAMBIA EL ESTADO A ACEPTADO
        else{
            log.info("Algunos productos no tienen stock suficiente. Pedido aceptado, pero no en preparación.", total);
            //VERIFICAR Y ACTUALIZAR STOCK
            pedido.setEstado(Estado.ACEPTADO);
        }
        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido getPedidoById(String id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    public void deletePedido(String id) {
        pedidoRepository.deleteById(id);
    }

}
