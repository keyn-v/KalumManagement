package edu.kalum.core.controllers;
import edu.kalum.core.model.dao.services.ICargoService;
import edu.kalum.core.model.entities.Cargo;
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
public class CargoController {
    private Logger logger = LoggerFactory.getLogger(CargoController.class);

    @Autowired
    private ICargoService cargoService;

    @GetMapping("/cargos")
    public ResponseEntity<?> listarCargos() {
        Map<String, Object> response = new HashMap<>();
        logger.debug("Iniciando Proceso de consulta de cargos");
        try {


            List<Cargo> cargos = cargoService.findAll();
            if (cargos == null && cargos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                logger.info("Se consulto la informacion de los cargos");
                return new ResponseEntity<List<Cargo>>(cargos, HttpStatus.OK);
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

    @GetMapping("/cargos/{cargoId}")
    public ResponseEntity<?> showCargo(@PathVariable String cargoId){
        Map<String,Object> response = new HashMap<>();
        logger.debug("iniciando proceso de busqueda de la carrera tecnica con id ".concat(cargoId));
        try {
            Cargo cargo = cargoService.findById(cargoId);
            if (cargoId == null) {
                logger.warn("No existe el cargo con el id ".concat(cargoId));
                response.put("Mensaje", "No existe el cargo con el id ".concat(cargoId));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                logger.info("se proceso la busqueda del cargo de forma exitosa");
                return new ResponseEntity<Cargo>(cargo, HttpStatus.OK);

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
