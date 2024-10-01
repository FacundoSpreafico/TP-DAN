package isi.dan.ms.pedidos.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import isi.dan.ms.pedidos.modelo.Estado;
import isi.dan.ms.pedidos.modelo.Pedido;

import java.util.List;

public interface PedidoRepository extends MongoRepository<Pedido, String> {

    List<Pedido> findByClienteIdAndEstadoIn(Integer clienteId, List<Estado> estados);
    
}

