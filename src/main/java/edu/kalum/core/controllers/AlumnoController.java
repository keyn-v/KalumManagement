package edu.kalum.core.controllers;
import edu.kalum.core.model.dao.services.IAlumnoService;
import edu.kalum.core.model.entities.Alumno;
import edu.kalum.core.model.entities.Cargo;
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
public class AlumnoController {
    /*nuevo*/
    @Value("${edu.kalum.core.configuration.page.size}")
    private Integer size;

    private Logger logger = LoggerFactory.getLogger(CargoController.class);

    @Autowired
    private IAlumnoService alumnoService;

    /*nuevo*/
    @GetMapping("/alumnos/page/{page}")
    public ResponseEntity<?> index(@PathVariable Integer page){
        Map<String,Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page,size);
            Page<Alumno> alumnoPage = alumnoService.findAll(pageable);
            if(alumnoPage == null && alumnoPage.getSize() == 0){
                logger.warn("No existen registros de Alumnos");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else {
                logger.info("Se proceso consulta de Alumno por pagina");
                return new ResponseEntity<Page<Alumno>>(alumnoPage, HttpStatus.OK);
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

    /*nuevo*/
    @PostMapping("/alumnos")
    public ResponseEntity<?> create(@Valid @RequestBody Alumno value, BindingResult result){
        Map<String,Object> response = new HashMap<>();
        if (result.hasErrors() == true){
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("errores",errores);
            logger.info("Se encontraron errores en validaciones en la peticion");
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
        }
        try {
            Alumno alumno = alumnoService.save(value);
            response.put("Mensaje","El Alumno ha sido creada exitosamente");
            response.put("Alumno",alumno);
            logger.info("Se creo un Alumno de forma exitosa");
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


    @PutMapping("/alumnos/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Alumno value, BindingResult result, @PathVariable String id){
        Map<String,Object> response = new HashMap<>();
        if (result.hasErrors() == true){
            List<String> errores = result.getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());
            response.put("errores",errores);
            logger.info("Se encontraron errores de validacion en la peticion");
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
        }
        try {
            Alumno alumno = alumnoService.findById(id);
            if (alumno == null){
                response.put("Mensaje","El alumno con el carne: ".concat(id).concat(" no existe"));
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.NOT_FOUND);
            }else {
                alumno.setApellidos(value.getApellidos());
                alumno.setNombres(value.getNombres());
                alumno.setDireccion(value.getDireccion());
                alumno.setTelefono(value.getTelefono());
                alumno.setEmail(value.getEmail());
                alumnoService.save(alumno);
                response.put("Mensaje","El alumno fue actualizada con exito");
                response.put("Alumno",alumno);
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
    @DeleteMapping("/alumnos/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Alumno alumno = alumnoService.findById(id);
            if (alumno == null) {
                response.put("Mensaje", "El alumno con el carne: ".concat(id).concat(" no existe"));
                logger.warn("No existen registros del alumno");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            } else {
                alumnoService.delete(alumno);
                response.put("Mensaje", "El alumno fue eliminada exitosamente");
                response.put("Alumno", alumno);
                logger.info("se elimino el alumno de forma exitosa");
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
