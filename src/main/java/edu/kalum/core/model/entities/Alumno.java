package edu.kalum.core.model.entities;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "alumno")
public class Alumno implements Serializable {
    @Id
    @Column(name = "carne")
    public String carne;
    @Column(name = "apellidos")
    public String apellidos;
    @Column(name = "nombres")
    public String nombres;
    @Column(name = "direccion")
    public String direccion;
    @Column(name = "telefono")
    public String telefono;
    @Column(name = "email")
    public String email;
}
