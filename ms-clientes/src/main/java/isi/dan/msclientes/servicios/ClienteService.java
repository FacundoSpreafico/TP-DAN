package isi.dan.msclientes.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.exception.ClienteNotFoundException;
import isi.dan.msclientes.model.Cliente;
import isi.dan.msclientes.model.Obra;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }

    public Boolean getSaldo(Integer id, BigDecimal montoTotal) throws ClienteNotFoundException{
        Optional<Cliente> cliente = this.findById(id);
        if(cliente.isPresent()){
        boolean saldoSuficiente = cliente.get().getMaximoDescubierto().doubleValue() >= montoTotal.doubleValue();
        return saldoSuficiente;
        }
        else{
            throw new ClienteNotFoundException("Cliente "+id+" no encontrado");
        }
    }
}
