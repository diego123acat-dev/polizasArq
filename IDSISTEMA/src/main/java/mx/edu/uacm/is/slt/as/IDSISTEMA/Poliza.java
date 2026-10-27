package mx.edu.uacm.is.slt.as.IDSISTEMA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad del dominio Poliza conforme al UML de la práctica.
 * Se asume que "clave" identifica de manera única a una póliza.
 */
@Entity
@Table(name = "poliza")
public class Poliza {

    @Id
    @Column(name = "clave", nullable = false, length = 80)
    private String clave;

    @Column(name = "tipo", nullable = false)
    private int tipo;

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    protected Poliza() {
        // Constructor requerido por JPA.
    }

    public Poliza(String clave, int tipo, double monto, String descripcion) {
        this.clave = clave;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}