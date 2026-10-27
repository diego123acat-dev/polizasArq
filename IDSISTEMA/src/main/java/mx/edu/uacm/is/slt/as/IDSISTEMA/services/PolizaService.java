package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import org.springframework.stereotype.Service;

import mx.uacm.polizas.client.SistemaDuenoClient;

@Service
public class PolizaService {

    private final SistemaDuenoClient sistemaDuenoClient;

    public PolizaService(SistemaDuenoClient sistemaDuenoClient) {
        this.sistemaDuenoClient = sistemaDuenoClient;
    }

    public String consultarPoliza(String clave) {
        return sistemaDuenoClient.consultarPoliza(clave);
    }

    public String consultarPolizasPorCliente(String curpCliente) {
        return sistemaDuenoClient.consultarPolizasPorCliente(curpCliente);
    }

    /*
     * La práctica permite dejar pendientes las peticiones de escritura al
     * Sistema Dueño en esta etapa. Estos métodos son marcadores para integrar
     * después la lógica acordada por el equipo.
     */
    public String registrarPoliza(String clave, int tipo, double monto,
                                  String descripcion, String curpCliente) {
        return "Operación de registro pendiente de integración con el Sistema Dueño.";
    }

    public String modificarPoliza(String clave, double monto, String descripcion) {
        return "Operación de modificación pendiente de integración con el Sistema Dueño.";
    }

    public String darDeBajaPoliza(String clave) {
        return "Operación de baja pendiente de integración con el Sistema Dueño.";
    }
}
