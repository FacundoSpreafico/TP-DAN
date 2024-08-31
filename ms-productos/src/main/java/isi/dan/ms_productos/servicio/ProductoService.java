package isi.dan.ms_productos.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import isi.dan.ms_productos.conf.RabbitMQConfig;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.dto.DescuentoUpdateDTO;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import java.util.List;
import org.springframework.messaging.handler.annotation.Payload;
@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    Logger log = LoggerFactory.getLogger(ProductoService.class);

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
    public void handleStockUpdate(@Payload String message) throws ProductoNotFoundException {
        log.info("Recibido {}", message);

        message = message.trim();

        String[] parts = message.split(";");
 
        log.info("Parts: {}", (Object)parts);

            try {
                Long productoId = Long.parseLong(parts[0]);
                Integer cantidad = Integer.parseInt(parts[1]);

                log.info("Producto ID: {}, Cantidad: {}", productoId, cantidad);

                Producto producto = productoRepository.findById(productoId).orElseThrow(() -> new ProductoNotFoundException(productoId));


                // Actualizar el stock del producto
                producto.setStockActual(producto.getStockActual() - cantidad);
                productoRepository.save(producto);

                log.info("Producto actualizado, stock actual: {}", producto.getStockActual());

                // Verificar el punto de pedido y generar un pedido si es necesario
                // if (producto.getStock() < producto.getPuntoDePedido()) {
                //     generarNuevoPedido(producto);
                // }

            } catch (NumberFormatException e) {
                log.error("Error al convertir la cantidad a número: {}", e.getMessage());
            }
        }

    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id) throws ProductoNotFoundException{
        return productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
    }

    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto updateDescuento (DescuentoUpdateDTO descuentoUpdateDTO) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(descuentoUpdateDTO.getId()).orElseThrow(() -> new ProductoNotFoundException(descuentoUpdateDTO.getId()));
        producto.setDescuentoPromocional(descuentoUpdateDTO.getDescuentoPromocional());
        productoRepository.save(producto);
        return producto;
    }

    public Producto updateStock (StockUpdateDTO stockUpdateDTO) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(stockUpdateDTO.getIdProducto()).orElseThrow(() -> new ProductoNotFoundException(stockUpdateDTO.getIdProducto()));
        producto.setStockActual(stockUpdateDTO.getCantidad());
        productoRepository.save(producto);
        return producto;
    }



}

