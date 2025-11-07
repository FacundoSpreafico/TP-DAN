package isi.dan.ms_productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isi.dan.ms_productos.dto.DescuentoUpdateDTO;
import isi.dan.ms_productos.dto.ProductoDTO;
import isi.dan.ms_productos.dto.StockUpdateDTO;
import isi.dan.ms_productos.exception.ProductoNotFoundException;
import isi.dan.ms_productos.exception.RestControllerException;
import isi.dan.ms_productos.model.Categoria;
import isi.dan.ms_productos.model.Producto;
import isi.dan.ms_productos.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductoController.class)
@Import(RestControllerException.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private Producto buildProducto(Long id) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre("Prod " + id);
        p.setDescripcion("Desc " + id);
        p.setPrecio(new BigDecimal("123.45"));
        p.setStockActual(10);
        p.setDescuentoPromocional(5);
        return p;
    }

    @Test
    @DisplayName("GET /api/productos devuelve lista 200")
    void getAllProductos_ok() throws Exception {
        given(productoService.getAllProductos()).willReturn(List.of(buildProducto(1L), buildProducto(2L)));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("GET /api/productos/{id} 200")
    void getProductoById_ok() throws Exception {
        given(productoService.getProductoById(1L)).willReturn(buildProducto(1L));

        mockMvc.perform(get("/api/productos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Prod 1")));
    }

    @Test
    @DisplayName("GET /api/productos/{id} 404 cuando no existe")
    void getProductoById_notFound() throws Exception {
        given(productoService.getProductoById(99L)).willThrow(new ProductoNotFoundException(99L));

        mockMvc.perform(get("/api/productos/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo", is(404)))
                .andExpect(jsonPath("$.description", containsString("Producto 99 no encontrado")));
    }

    @Test
    @DisplayName("POST /api/productos crea 201")
    void createProducto_created() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Nuevo");
        dto.setDescripcion("desc");
        dto.setPrecio(new BigDecimal("10.00"));
        dto.setStockActual(3);
        dto.setDescuentoPromocional(0);

        Producto created = buildProducto(10L);
        given(productoService.createProducto(any(ProductoDTO.class))).willReturn(created);
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nombre", is("Prod 10")));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} 204")
    void deleteProducto_noContent() throws Exception {
        doNothing().when(productoService).deleteProducto(1L);

        mockMvc.perform(delete("/api/productos/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PUT /api/productos/{id}/stock?cantidad= 200 devuelve boolean")
    void updateStockProducto_ok() throws Exception {
        given(productoService.updateStock(any(StockUpdateDTO.class))).willReturn(true);

        mockMvc.perform(put("/api/productos/{id}/stock", 5).param("cantidad", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("PUT /api/productos/provision 200 con cuerpo StockUpdateDTO")
    void updateProvision_ok() throws Exception {
        StockUpdateDTO body = new StockUpdateDTO();
        body.setIdProducto(1L);
        body.setCantidad(20);
        body.setPrecio(new BigDecimal("99.99"));

        Producto actualizado = buildProducto(1L);
        actualizado.setPrecio(new BigDecimal("99.99"));
        actualizado.setStockActual(20);

        given(productoService.updateStockPrecio(any(StockUpdateDTO.class))).willReturn(actualizado);

        mockMvc.perform(put("/api/productos/provision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio", is(99.99)))
                .andExpect(jsonPath("$.stockActual", is(20)));
    }

    @Test
    @DisplayName("PUT /api/productos/descuento 200 con cuerpo DescuentoUpdateDTO")
    void updateDescuento_ok() throws Exception {
        DescuentoUpdateDTO body = new DescuentoUpdateDTO();
        body.setId(1L);
        body.setDescuentoPromocional(15);

        Producto actualizado = buildProducto(1L);
        actualizado.setDescuentoPromocional(15);

        given(productoService.updateDescuento(any(DescuentoUpdateDTO.class))).willReturn(actualizado);

        mockMvc.perform(put("/api/productos/descuento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descuentoPromocional", is(15)));
    }

    @Test
    @DisplayName("PUT /api/productos/{id} 200 actualiza producto")
    void updateProducto_ok() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Editado");

        Producto updated = buildProducto(1L);
        updated.setNombre("Editado");

        given(productoService.updateProducto(eq(1L), any(ProductoDTO.class))).willReturn(updated);

        mockMvc.perform(put("/api/productos/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Editado")));
    }

    @Test
    @DisplayName("GET /api/productos/{id}/precio 200")
    void getPrecio_ok() throws Exception {
        given(productoService.getPrecio(1L)).willReturn(new BigDecimal("12.34"));

        mockMvc.perform(get("/api/productos/{id}/precio", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("12.34"));
    }

    @Test
    @DisplayName("GET /api/productos/{id}/descuento 200")
    void getDescuento_ok() throws Exception {
        given(productoService.getDescuento(1L)).willReturn(7);

        mockMvc.perform(get("/api/productos/{id}/descuento", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    @DisplayName("GET /api/productos/categorias 200 lista categorías")
    void getCategoriasDesdeProductos_ok() throws Exception {
        Categoria c = new Categoria();
        c.setId(1L);
        c.setNombre("Cat 1");
        given(productoService.getAllCategorias()).willReturn(List.of(c));

        mockMvc.perform(get("/api/productos/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Cat 1")));
    }
}
