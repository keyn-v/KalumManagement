package edu.kalum.core.model.dao.services;
import edu.kalum.core.model.entities.Cargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ICargoService {
    public List<Cargo> findAll();
    /*nuevo*/
    public Page<Cargo> findAll(Pageable pageable);
    public Cargo findById(String cargoId);
    /*nuevo*/
    public Cargo save(Cargo cargo);
    public void delete(Cargo cargo);
}
