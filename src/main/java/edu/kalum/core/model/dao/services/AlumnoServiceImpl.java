package edu.kalum.core.model.dao.services;
import edu.kalum.core.model.dao.IAlumnoDao;
import edu.kalum.core.model.entities.Alumno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AlumnoServiceImpl implements IAlumnoService{

    @Autowired
    private IAlumnoDao alumnoDao;

    @Override
    public List<Alumno> findAll() {
        return alumnoDao.findAll();
    }

    @Override
    public Alumno findById(String carne) {
        return alumnoDao.findById(carne).orElse(null);
    }
}
