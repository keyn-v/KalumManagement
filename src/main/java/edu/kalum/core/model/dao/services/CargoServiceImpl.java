package edu.kalum.core.model.dao.services;
import edu.kalum.core.model.dao.ICargoDao;
import edu.kalum.core.model.entities.Cargo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CargoServiceImpl implements ICargoService{

    @Autowired
    private ICargoDao cargoDao;

    @Override
    public List<Cargo> findAll() {
        return cargoDao.findAll();
    }

    @Override
    public Cargo findById(String cargoId) {
        return cargoDao.findById(cargoId).orElse(null);
    }
}
