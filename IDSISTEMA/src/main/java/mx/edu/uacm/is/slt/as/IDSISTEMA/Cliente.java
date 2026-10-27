package mx.edu.uacm.is.slt.as.IDSISTEMA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @Column(nullable = false, unique = true, length = 18)
    private String curp;

    @Column(nullable = false)
    private String nombres;

    @Column(name = "primer_apellido", nullable = false)
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    private String direccion;

    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    // Constructor vacío obligatorio para Hibernate/JPA
    public Cliente() {
    }

    // Constructor con parámetros
    public Cliente(String curp, String nombres, String primerApellido, String segundoApellido, String direccion, Date fechaNacimiento) {
        this.curp = curp;
        this.nombres = nombres;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.direccion = direccion;
        this.fechaNacimiento = (fechaNacimiento != null) ? new Date(fechaNacimiento.getTime()) : null;
    }

    // Getters y Setters
    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Date getFechaNacimiento() { 
        return fechaNacimiento != null ? new Date(fechaNacimiento.getTime()) : null; 
    }
    public void setFechaNacimiento(Date fechaNacimiento) { 
        this.fechaNacimiento = fechaNacimiento != null ? new Date(fechaNacimiento.getTime()) : null; 
    }

    @Override
    public String toString() {
        return "Cliente{curp='" + curp + "', nombres='" + nombres 
                + "', primerApellido='" + primerApellido + "', segundoApellido='" + segundoApellido 
                + "', direccion='" + direccion + "', fechaNacimiento=" + fechaNacimiento + "}";
    }
}