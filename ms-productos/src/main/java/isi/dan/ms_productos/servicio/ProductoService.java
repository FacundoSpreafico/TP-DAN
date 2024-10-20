
package isi.dan.ms_productos.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import isi.dan.ms_productos.conf.RabbitMQConfig;
import isi.dan.ms_productos.dao.ProductoRepository;
import isi.dan.ms_productos.dto.DescuentoUpdateDTO;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.exception.ProductoNotStockException;
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

    public Producto updateStockPrecio (StockUpdateDTO stockUpdateDTO) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(stockUpdateDTO.getIdProducto()).orElseThrow(() -> new ProductoNotFoundException(stockUpdateDTO.getIdProducto()));
        producto.setStockActual(stockUpdateDTO.getCantidad());
        producto.setPrecio(stockUpdateDTO.getPrecio());
        productoRepository.save(producto);
        return producto;
    }

    public Boolean updateStock(StockUpdateDTO stockUpdateDTO) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(stockUpdateDTO.getIdProducto()).orElseThrow(() -> new ProductoNotFoundException(stockUpdateDTO.getIdProducto()));
        if (producto.getStockActual() < stockUpdateDTO.getCantidad()) {
            return false;
            //PEDIDO ESTADO ACEPTADO
        }
        else{
            //PEDIDO ESTADO EN_PREPARACION
            producto.setStockActual(producto.getStockActual() - stockUpdateDTO.getCantidad());
            productoRepository.save(producto);
            return true;
        }
    }

    public BigDecimal getPrecio(Long id) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
        return producto.getPrecio();
    }

    public Integer getDescuento(Long id) throws ProductoNotFoundException {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
        return producto.getDescuentoPromocional();
    }

    @RabbitListener(queues = RabbitMQConfig.DEVOLVER_STOCK_QUEUE)
    public void updateStockAsinc(@Payload String message) throws ProductoNotFoundException{
        log.info("Recibido mensaje: {}", message);

        String[] parts = message.split(";");
        Long productoId = Long.parseLong(parts[0]);
        Integer cantidad = Integer.parseInt(parts[1]);
    
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new ProductoNotFoundException(productoId));
    
        producto.setStockActual(producto.getStockActual() + cantidad);
        productoRepository.save(producto);
    
        log.info("Stock actualizado, stock actual: {}", producto.getStockActual());
    }
    

}

