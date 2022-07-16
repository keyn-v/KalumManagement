package edu.kalum.core.controllers;
import edu.kalum.core.model.dao.services.IJornadaService;
import edu.kalum.core.model.entities.Cargo;
import edu.kalum.core.model.entities.Jornada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "kalum-management/v1")
public class JornadaController {
    /*nuevo*/
    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    private Logger logger = LoggerFactory.getLogger(JornadaController.class);

    @Autowired
    private IJornadaService jornadaService;

    /*nuevo*/
    @GetMapping("/jornadas/page/{page}")
    public ResponseEntity<?> index(@PathVariable Integer page){
        Map<String,Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<Jornada> jornadaPage = jornadaService.findAll(pageable);
            if(jornadaPage == null && jornadaPage.getSize() == 0){
                logger.warn("No existen registros de Jornada");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else {
                logger.info("Se proceso consulta de Jornada por pagina");
                return new ResponseEntity<Page<Jornada>>(jornadaPage, HttpStatus.OK);
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

    /*nuevo*/
    @PostMapping("/jornadas")
    public ResponseEntity<?> create(@Valid @RequestBody Jornada value, BindingResult result){
        Map<String,Object> response = new HashMap<>();
        if (result.hasErrors() == true){
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("errores",errores);
            logger.info("Se encontraron errores en validaciones en la peticion");
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
        }
        try {
            value.setJornadaId(UUID.randomUUID().toString());
            Jornada jornada = jornadaService.save(value);
            response.put("Mensaje","La jornada ha sido creada exitosamente");
            response.put("Jornada",jornada);
            logger.info("Se creo un Cargo de forma exitosa");
            return new ResponseEntity<Map<String ,Object>>(response,HttpStatus.CREATED);
        }catch (CannotCreateTransactionException e){
            logger.error("error al momento de conectarse a la base de datos");
            response.put("Mensaje","error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }catch (DataAccessException e){
            logger.error("Error al momento de crear el nuevo registro");
            response.put("Mensaje","Error al momento de crear el nuevo registro");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }

    }


    @PutMapping("/jornadas/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Jornada value, BindingResult result, @PathVariable String id){
        Map<String,Object> response = new HashMap<>();
        if (result.hasErrors() == true){
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("errores",errores);
            logger.info("Se encontraron errores de validacion en la peticion");
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
        }
        try {
            Jornada jornada = jornadaService.findById(id);
            if (jornada == null){
                response.put("Mensaje","La jornada con el id: ".concat(id).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
            }else {
                jornada.setJornada(value.getJornada());
                jornada.setDescripcion(value.getDescripcion());
                jornadaService.save(jornada);
                response.put("Mensaje","La jornada fue actualizada con exito");
                response.put("Jornada",jornada);
                logger.info("se actualizo la informacion de manera correcta");
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
            }
        }catch (CannotCreateTransactionException e){
            logger.error("error al momento de conectarse a la base de datos");
            response.put("Mensaje","error al momento de conectarse a la base de datos");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }catch (DataAccessException e){
            logger.error("Error al momento de actualizar la informacion");
            response.put("Mensaje","Error al momento de actualizar la informacion");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }

    }

    /*nuevo*/
    @DeleteMapping("/jornadas/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Jornada jornada = jornadaService.findById(id);
            if (jornada == null) {
                response.put("Mensaje", "La jornada con el id: ".concat(id).concat(" no existe"));
                logger.warn("No existen registros de la jornada");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                jornadaService.delete(jornada);
                response.put("Mensaje", "La jornada fue eliminada exitosamente");
                response.put("Jornada", jornada);
                logger.info("se elimino la jornada de forma exitosa");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
            }
        } catch (CannotCreateTransactionException e) {
            logger.error("error al momento de conectarse a la base de datos");
            response.put("Mensaje", "error al momento de conectarse a la base de datos");
            response.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (DataAccessException e) {
            logger.error("Error al momento de eliminar el registro");
            response.put("Mensaje", "Error al momento de eliminar el registro");
            response.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}


