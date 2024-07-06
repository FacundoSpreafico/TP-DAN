package isi.dan.msclientes.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.micrometer.observation.Observation;
import isi.dan.msclientes.dao.ObraRepository;
import isi.dan.msclientes.model.Obra;
import isi.dan.msclientes.model.Estado;
import java.util.List;
import java.util.Optional;

@Service
public class ObraService {
    
    @Autowired
    private ObraRepository obraRepository;

    public List<Obra> findAll() {
        return obraRepository.findAll();
    }

    public Optional<Obra> findById(Integer id) {
        return obraRepository.findById(id);
    }

    public Obra save(Obra obra) {
        return obraRepository.save(obra);
    }

    public Obra update(Obra obra) {
        return obraRepository.save(obra);
    }

    public void deleteById(Integer id) {
        obraRepository.deleteById(id);
    }

    public void finalizarObra(Integer id) {
        Obra obra = obraRepository.findById(id).orElseThrow(() -> new RuntimeException("Obra no encontrada"));
        if (Estado.HABILITADA.equals(obra.getEstado())) {
            obra.setEstado(Estado.FINALIZADA);
            update(obra);
            habilitarProximaObra(id);
        } else {
            throw new IllegalStateException("La obra ya está finalizada o no está en estado pendiente");
        }
    }

    public void habilitarProximaObra (Integer idCliente){
        Optional<Obra> obraPendiente = obtenerObrasPendientes(idCliente);
        if (obraPendiente.isPresent()){
            Obra obra = obraPendiente.get();
            obra.setEstado(Estado.HABILITADA);
            update(obra);
        }
    }

    public Optional<Obra> obtenerObrasPendientes(Integer idCliente) {
       List<Obra> obras = obraRepository.findByidCliente(idCliente);
       Optional<Obra> obraPendiente = obras.stream()
                        .filter(o -> o.getEstado() == Estado.PENDIENTE)
                        .findFirst();
         return obraPendiente;
    }

    public void updateEstado(Integer id, Estado estado) {
        Obra obra = obraRepository.findById(id).orElseThrow(() -> new RuntimeException("Obra no encontrada"));
        obra.setEstado(estado);
        update(obra);
    }

    //CHECKEAR

    public void updateHabilitado (Integer id){
        Obra obra = obraRepository.findById(id).orElseThrow(() -> new RuntimeException("Obra no encontrada"));
        if (obra.getCliente().getMaximaCantidadObras() >= obraRepository.findByidCliente(obra.getCliente().getId()).size()){
            throw new RuntimeException("No se puede asignar mas obras a este cliente");
        }
    }
}

