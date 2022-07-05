package edu.kalum.core.controllers;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.kalum.core.model.dao.services.IAspiranteService;
import edu.kalum.core.model.dao.services.ICarreraTecnicaService;
import edu.kalum.core.model.dtos.EnrollmentRequestDTO;
import edu.kalum.core.model.entities.Aspirante;
import edu.kalum.core.model.entities.CarreraTecnica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "kalum-management/v1")
public class InscripcionController {

    private Logger logger = LoggerFactory.getLogger(InscripcionController.class);

    @Autowired
    private ICarreraTecnicaService carreraTecnicaService;

    @Autowired
    private IAspiranteService aspiranteService;

    @GetMapping("inscripcion")
    public ResponseEntity<String> test(){
        return new ResponseEntity<String>("{message: 'Testing inscripcion}",HttpStatus.OK);
    }
    @PostMapping("/inscripciones/enrollment")
    public ResponseEntity<?> enrollmentProcess(@Valid @RequestBody EnrollmentRequestDTO request, BindingResult result){
        Map<String, Object> response = new HashMap<>();
        if(result.hasErrors()){
            List<String> errores = result.getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errores",errores);
            return new ResponseEntity<Map<String ,Object>>(response, HttpStatus.BAD_REQUEST);
        }
        try {
            Aspirante aspirante = aspiranteService.findById(request.getNoExpediente());
            if (aspirante == null) {
                logger.warn("No existe el aspirante con el numero de expediente".concat(request.getNoExpediente()));
                response.put("Mensaje", "No existe el aspirante con el numero de expediente".concat(request.getNoExpediente()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            CarreraTecnica carreraTecnica = carreraTecnicaService.findById(request.getCarreraId());
            if (carreraTecnica == null) {
                logger.warn("no existe la carrera tecnica con el id".concat(request.getCarreraId()));
                response.put("Mensaje", "no existe la carrera tecnica con el id".concat(request.getCarreraId()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
            }
            boolean respuesta = crearSolicitudInscripcion(request);
            if (respuesta == true) {
                response.put("Message","la solicitud de inscripcion fue generada con exito");
                return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
            }else {
                response.put("Mensaje","Error al momento de crear la solicitud de inscripcion con el pexpediente ".concat(request.getNoExpediente()));
                response.put("Error","Error al momento de escribir en la cola");
                return new ResponseEntity<Map<String ,Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
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
        }catch (Exception e){
            logger.error("Error interno del servicio Inscripcion");
            response.put("Mensaje","Error interno del servicio Inscripcion");
            response.put("Error",e.getMessage().concat(": ").concat(e.getMessage()));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.SERVICE_UNAVAILABLE);
        }

    }
    private boolean crearSolicitudInscripcion(EnrollmentRequestDTO request) throws Exception{
        boolean respuesta = false;
        Connection conexion = null;
        Channel channel = null;
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");
            try {
                conexion = factory.newConnection();
                channel = conexion.createChannel();
                String message = new Gson().toJson(request);
                channel.basicPublish("dev.exchange-enrollment", "", null, message.getBytes(StandardCharsets.UTF_8));
                respuesta = true;
            }catch (Exception e){
                logger.error("Error al escribir");
            }
            channel.close();
            conexion.close();
            return respuesta;

    }
}
