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
import isi.dan.ms.pedidos.servicio.SequenceGenerator;
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

    @Autowired
    private SequenceGenerator sequenceGenerator;
    Logger log = LoggerFactory.getLogger(PedidoService.class);

    public Pedido savePedido(Pedido pedido) {

    pedido.setNumeroPedido((int) sequenceGenerator.generateSequence(Pedido.SEQUENCE_NAME));

    BigDecimal total = BigDecimal.ZERO;
    boolean stockSuficiente = true;
   
    pedido.setFecha(ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires")).toInstant());

    for (DetallePedido dp : pedido.getDetalle()) {
        dp.setPrecioUnitario(productoFeign.getPrecio(dp.getProducto().getId()));
        dp.setDescuento(productoFeign.getDescuento(dp.getProducto().getId()));
        if (dp.getDescuento() != 0) {
            // Calcular el precio total sin descuento
            BigDecimal precioTotal = dp.getPrecioUnitario().multiply(BigDecimal.valueOf(dp.getCantidad()));
            
            // Calcular el porcentaje de descuento (ejemplo: si dp.getDescuento() es 10, representa el 10%)
            BigDecimal porcentajeDescuento = BigDecimal.valueOf(dp.getDescuento()).divide(BigDecimal.valueOf(100));
            
            // Calcular el monto de descuento
            BigDecimal montoDescuento = precioTotal.multiply(porcentajeDescuento);
            
            // Restar el monto del descuento al precio total
            BigDecimal precioFinalConDescuento = precioTotal.subtract(montoDescuento);
            
            // Establecer el precio final en el detalle del pedido
            dp.setPrecioFinal(precioFinalConDescuento);
        } else {
            // Si no hay descuento, calcular el precio final normalmente
            dp.setPrecioFinal(dp.getPrecioUnitario().multiply(BigDecimal.valueOf(dp.getCantidad())));
        }
        total = total.add(dp.getPrecioFinal());
    }

    for (DetallePedido dp : pedido.getDetalle()) {
        log.info("Verificando y actualizando stock para el producto {}", dp.getProducto().getId());
        Boolean stockActualizado = productoFeign.updateStockProducto(dp.getProducto().getId(), dp.getCantidad());

        if (!stockActualizado) {
            stockSuficiente = false;  // Si algún producto no tiene stock, lo marcamos
            break;
        }
    }

    try{
    //VERIFICAR SALDO DEL CLIENTE
    if (!verificarSaldoCliente(pedido.getCliente().getId(), total)) {
        pedido.setTotal(total);
        pedido.setEstado(Estado.RECHAZADO);
        log.info("Pedido rechazado. El cliente no tiene saldo. Enviado desde savePedido (PedidoService)", total);
        return pedidoRepository.save(pedido);
    }
    else{
        if (stockSuficiente){
            // SI HAY STOCK SUFICIENTE DE TODOS LOS PRODUCTOS, SE CAMBIA EL ESTADO A EN PREPARACION
            log.info("Pedido en preparación. Enviado desde savePedido (PedidoService)", total);
            pedido.setEstado(Estado.EN_PREPARACION);
        }
       
        else{
             // SI NO HAY STOCK SUFICIENTE DE ALGUN PRODUCTO, SE CAMBIA EL ESTADO A ACEPTADO
            log.info("Algunos productos no tienen stock suficiente. Pedido aceptado, pero no en preparación.", total);
            pedido.setEstado(Estado.ACEPTADO);
        }
    }
    }
    catch(Exception e){
        log.error("Error al verificar saldo del cliente. Total del pedido: {}. Error: {}", total, e.getMessage(), e);
        pedido.setEstado(Estado.RECIBIDO);
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

    public boolean verificarSaldoCliente(Integer clienteId, BigDecimal montoPedido) {
        // Obtener los pedidos previos del cliente en estado ACEPTADO o EN_PREPARACION
        List<Pedido> pedidosPrevios = pedidoRepository.findByClienteIdAndEstadoIn(clienteId, List.of(Estado.ACEPTADO, Estado.EN_PREPARACION));
    
        for (Pedido pedidos: pedidosPrevios){
            log.info("Pedidos previos: {}", pedidos.getNumeroPedido());
        }

        // Calcular el total de los pedidos previos
        BigDecimal totalPedidosPrevios = pedidosPrevios.stream()
            .map(Pedido::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener el máximo descubierto del cliente
        BigDecimal maximoDescubierto = clienteFeign.obtenerMaximoDescubierto(clienteId);
    
        // Calcular si el nuevo pedido supera el límite de descubierto
        BigDecimal totalConNuevoPedido = totalPedidosPrevios.add(montoPedido);
        
        return totalConNuevoPedido.compareTo(maximoDescubierto) <= 0; 

    }
}
