package edu.kalum.core.controllers;
import edu.kalum.core.model.dao.services.IJornadaService;
import edu.kalum.core.model.entities.Jornada;
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
public class JornadaController {
    private Logger logger = LoggerFactory.getLogger(JornadaController.class);

    @Autowired
    private IJornadaService jornadaService;

    @GetMapping(value = "/jornadas")
    public ResponseEntity<?> listarJornadas() {
        Map<String, Object> response = new HashMap<>();
        logger.debug("Iniciando Proceso de consulta de jornadas");
        try {


            List<Jornada> jornada = jornadaService.findAll();
            if (jornada != null && jornada.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se consulto la informacion de las jornadas");
                return new ResponseEntity<List<Jornada>>(jornada, HttpStatus.OK);
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

    @GetMapping(value = "/jornadas/{jornadaId}")
    public ResponseEntity<?> showJornada(@PathVariable String jornadaId){
        Map<String,Object> response = new HashMap<>();
        logger.debug("iniciando proceso de busqueda de la jornada con Id ".concat(jornadaId));
        try {
            Jornada jornada = jornadaService.findById(jornadaId);
            if (jornada == null) {
                logger.warn("No existe la jornada con el id ".concat(jornadaId));
                response.put("Mensaje", "No existe la jornada con el id ".concat(jornadaId));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.info("se proceso la busqueda de la jornada de forma exitosa");
                return new ResponseEntity<Jornada>(jornada, HttpStatus.OK);
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


