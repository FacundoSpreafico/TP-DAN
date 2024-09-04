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
import isi.dan.ms.pedidos.feign.ClienteFeignClient;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ClienteFeignClient clienteFeign;

    Logger log = LoggerFactory.getLogger(PedidoService.class);

    public Pedido savePedido(Pedido pedido) {
    BigDecimal total = BigDecimal.ZERO;

    //FALTA AGREGAR LA LOGICA PARA AÑADIR EL NUMERO DE PEDIDO
    pedido.setFecha(Instant.now());

        for( DetallePedido dp : pedido.getDetalle()){
            dp.setPrecioFinal(dp.getPrecioUnitario().multiply(BigDecimal.valueOf(dp.getCantidad())));
            total = total.add(dp.getPrecioFinal());
            log.info("Enviando {}", dp.getProducto().getId()+";"+dp.getCantidad());
            rabbitTemplate.convertAndSend(RabbitMQConfig.STOCK_UPDATE_QUEUE, dp.getProducto().getId()+";"+dp.getCantidad());
        }

        
        pedido.setTotal(total);
        if (!clienteFeign.verificarSaldoCliente(pedido.getCliente().getId(), total)){
            pedido.setEstado(Estado.RECHAZADO);
            log.info("Pedido no guardado. El cliente no tiene saldo. Enviado desde savePedido (PedidoService)", total);
        }
        else{
            log.info("Pedido guardado. Enviado desde savePedido (PedidoService)", total);
            //VERIFICAR Y ACTUALIZAR STOCK
            return pedidoRepository.save(pedido);
        }
        return null;
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
