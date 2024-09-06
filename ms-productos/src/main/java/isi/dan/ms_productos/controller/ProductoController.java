package isi.dan.ms_productos.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import isi.dan.ms_productos.aop.LogExecutionTime;
import isi.dan.ms_productos.dto.DescuentoUpdateDTO;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.modelo.Producto;
import isi.dan.ms_productos.servicio.EchoClientFeign;
import isi.dan.ms_productos.servicio.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    EchoClientFeign echoSvc;

    @PostMapping
    @LogExecutionTime
    public ResponseEntity<Producto> createProducto(@RequestBody @Validated Producto producto) {
        Producto savedProducto = productoService.saveProducto(producto);
        return ResponseEntity.ok(savedProducto);
    }

    @GetMapping
    @LogExecutionTime
    public List<Producto> getAllProductos() {
        return productoService.getAllProductos();
    }

    @GetMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) throws ProductoNotFoundException {
        return  ResponseEntity.ok(productoService.getProductoById(id));
    }

    @DeleteMapping("/{id}")
    @LogExecutionTime
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/provision")
    @LogExecutionTime
    public ResponseEntity<Producto> updateProducto(@RequestBody StockUpdateDTO stockUpdateDTO) throws ProductoNotFoundException {
        return ResponseEntity.ok(productoService.updateStockPrecio(stockUpdateDTO));
    }

    @PutMapping("/descuento")
    @LogExecutionTime
    public ResponseEntity<Producto> updateDescuento(@RequestBody DescuentoUpdateDTO descuento) throws ProductoNotFoundException {
        return ResponseEntity.ok(productoService.updateDescuento(descuento)); 
    }

    @PutMapping("/{id}/stock")
    @LogExecutionTime
    public ResponseEntity<Boolean> updateStockProducto(@PathVariable Long id, @RequestParam Integer cantidad) throws ProductoNotFoundException {
        StockUpdateDTO stockUpdateDTO = new StockUpdateDTO();
        stockUpdateDTO.setIdProducto(id);
        stockUpdateDTO.setCantidad(cantidad);
        return ResponseEntity.ok(productoService.updateStock(stockUpdateDTO));
    }
}

