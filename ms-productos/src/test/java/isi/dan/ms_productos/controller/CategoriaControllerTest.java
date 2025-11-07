package isi.dan.ms_productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import isi.dan.ms_productos.model.Categoria;
import isi.dan.ms_productos.service.CategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    private Categoria buildCategoria(Long id) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre("Cat " + id);
        return c;
    }

    @Test
    @DisplayName("POST /api/categorias 200 crea categoría")
    void createCategoria_ok() throws Exception {
        Categoria in = new Categoria();
        in.setNombre("Nueva");

        given(categoriaService.saveCategoria(any(Categoria.class))).willReturn(buildCategoria(10L));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(in)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.nombre", is("Cat 10")));
    }

    @Test
    @DisplayName("GET /api/categorias 200 lista")
    void getAllCategorias_ok() throws Exception {
        given(categoriaService.getAllCategorias()).willReturn(List.of(buildCategoria(1L), buildCategoria(2L)));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("GET /api/categorias/{id} 200 cuando existe, 404 cuando no")
    void getCategoriaById_ok_y_notFound() throws Exception {
        given(categoriaService.getCategoriaById(1L)).willReturn(buildCategoria(1L));
        given(categoriaService.getCategoriaById(99L)).willReturn(null);

        mockMvc.perform(get("/api/categorias/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Cat 1")));

        mockMvc.perform(get("/api/categorias/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} 204")
    void deleteCategoria_noContent() throws Exception {
        doNothing().when(categoriaService).deleteCategoria(1L);

        mockMvc.perform(delete("/api/categorias/{id}", 1))
                .andExpect(status().isNoContent());
    }
}

