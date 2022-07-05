package edu.kalum.core.controllers;
import edu.kalum.core.model.dao.services.IAlumnoService;
import edu.kalum.core.model.entities.Alumno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "kalum-management/v1")
public class AlumnoController {
    private Logger logger = LoggerFactory.getLogger(CargoController.class);

    @Autowired
    private IAlumnoService alumnoService;

    @GetMapping("/alumnos")
    public ResponseEntity<?> listarAlumnos() {
        Map<String, Object> response = new HashMap<>();
        logger.debug("Iniciando Proceso de consulta de alumnos");
        try {


            List<Alumno> alumnos = alumnoService.findAll();
            if (alumnos != null && alumnos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se consulto la informacion de los alumnos");
                return new ResponseEntity<List<Alumno>>(alumnos, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e) {
            logger.error("error al momento de conectarse a la base de datos");
            response.put("Mensaje", "error al momento de conectarse a la base de datos");
            response.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e) {
            logger.error("Error al momento de ejecutar la consulta a la base de datos");
            response.put("Mensaje", "Error al momento de ejecutar la consulta a la base de datos");
            response.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    @GetMapping("/alumnos/{carne}")
    public ResponseEntity<?> showAlumno(@PathVariable String carne){
        Map<String,Object> response = new HashMap<>();
        logger.debug("iniciando proceso de busqueda del alumno con carne ".concat(carne));
        try {
             Alumno alumno = alumnoService.findById(carne);
             if (carne == null) {
                logger.warn("No existe el alumno con el carne ".concat(carne));
                response.put("Mensaje", "No existe el carne con el id ".concat(carne));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
             } else {
                 logger.info("se proceso la busqueda del cargo de forma exitosa");
                 return new ResponseEntity<Alumno>(alumno, HttpStatus.OK);

             }
        }catch (CannotCreateTransactionException e){
            logger.error("error al momento de conectarse a la base de datos");
            response.put("Mensaje","error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }catch (DataAccessException e){
            logger.error("Error al momento de ejecutar la consulta a la base de datos");
            response.put("Mensaje","Error al momento de ejecutar la consulta a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
