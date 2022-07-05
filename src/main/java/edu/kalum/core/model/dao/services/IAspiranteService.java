package edu.kalum.core.model.dao.services;

import edu.kalum.core.model.entities.Aspirante;
import edu.kalum.core.model.entities.CarreraTecnica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAspiranteService {
    public List<Aspirante> findAll();
    public Page<Aspirante> findAll(Pageable pageable);
    public Aspirante findById(String noExpediente);
    public Aspirante save(Aspirante nombre);
    public void delete(Aspirante nombre);
}
