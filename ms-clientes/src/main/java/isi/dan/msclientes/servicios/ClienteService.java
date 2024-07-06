package isi.dan.msclientes.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import isi.dan.msclientes.dao.ClienteRepository;
import isi.dan.msclientes.model.Cliente;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    private BigDecimal numeroceroBD = new BigDecimal(0) ;

    @Value("${cliente.maximo_descubierto.default}")
    private BigDecimal defaultmd;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
  
        if(cliente.getMaximoDescubierto().compareTo(numeroceroBD)==-1){

        //aca falta poner el retutn bien con el error que tira
            return cliente;
        }
        //si es null le pone el default y si es mayor a 0 lo guarda normalmente
        else {
            if(cliente.getMaximoDescubierto()==null){
                cliente.setMaximoDescubierto(defaultmd);
            }
            return clienteRepository.save(cliente);
        }
    }

    public Cliente update(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }
}
 /*committest*/
