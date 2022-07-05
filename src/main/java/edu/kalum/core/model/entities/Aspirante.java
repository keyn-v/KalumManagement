package edu.kalum.core.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity
@Table(name = "aspirante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aspirante implements Serializable {
    @Id
    @NotEmpty(message = "el campo numero de expediente no debe ser vacio")
    @Column(name = "no_expediente")
    private String noExpediente;
    @NotEmpty
    @Column(name = "apellidos")
    private String apellidos;
    @NotEmpty
    @Column(name = "nombres")
    private String nombres;
    @NotEmpty
    @Column(name = "direccion")
    private String direccion;
    @NotEmpty
    @Column(name = "telefono")
    private String telefono;
    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "estatus")
    private String estatus = "NO ASIGNADO";
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carrera_id", referencedColumnName = "carrera_id")
    private CarreraTecnica carreraTecnica;
    //@Column(name = "jornada_id")
    //private String jornadaid;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jornada_id", referencedColumnName = "jornada_id")
    private Jornada jornada;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examen_id", referencedColumnName = "examen_id")
    private ExamenAdmision examenAdmision;
}
