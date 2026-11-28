package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import mx.edu.uacm.is.slt.as.IDSISTEMA.ClienteRepository;
import mx.edu.uacm.is.slt.as.IDSISTEMA.Poliza;
import mx.edu.uacm.is.slt.as.IDSISTEMA.PolizaRepository;
import mx.uacm.polizas.client.SistemaDuenoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PolizaService {

    private final SistemaDuenoClient sistemaDuenoClient;

    @Autowired(required = false)
    private PolizaRepository polizaRepository;

    @Autowired(required = false)
    private ClienteRepository clienteRepository;

    public PolizaService(SistemaDuenoClient sistemaDuenoClient) {
        this.sistemaDuenoClient = sistemaDuenoClient;
    }

    public String consultarPoliza(String clave) {
        try {
            if (clave == null || clave.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: Debe ingresar la clave de la póliza.";
            }
            return sistemaDuenoClient.consultarPoliza(clave.trim());
        } catch (Exception ex) {
            return "[EX-DND]: El Sistema Dueño no está disponible para consultar la póliza (" + ex.getMessage() + ").";
        }
    }

    public String consultarPolizasPorCliente(String curpCliente) {
        try {
            if (curpCliente == null || curpCliente.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: Debe ingresar la CURP del cliente.";
            }
            return sistemaDuenoClient.consultarPolizasPorCliente(curpCliente.trim().toUpperCase());
        } catch (Exception ex) {
            return "[EX-DND]: El Sistema Dueño no está disponible para consultar pólizas del cliente (" + ex.getMessage() + ").";
        }
    }

    public String registrarPoliza(String clave, int tipo, double monto,
                                  String descripcion, String curpCliente) {
        try {
            if (clave == null || clave.isBlank() || curpCliente == null || curpCliente.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: La clave de póliza y la CURP del cliente son obligatorias.";
            }
            if (monto <= 0) {
                return "[ERROR DE VALIDACIÓN]: El monto asegurado debe ser mayor a 0.";
            }

            String curpNormalizada = curpCliente.trim().toUpperCase();
            if (clienteRepository != null && !clienteRepository.existsById(curpNormalizada)) {
                return "[EX-CNR]: El cliente con CURP " + curpNormalizada + " no está registrado.";
            }

            String claveNormalizada = clave.trim();
            if (polizaRepository != null && polizaRepository.existsById(claveNormalizada)) {
                return "[EX-PYR]: La póliza con clave " + claveNormalizada + " ya está registrada.";
            }

            if (polizaRepository != null) {
                Poliza poliza = new Poliza(
                        claveNormalizada, tipo, monto,
                        descripcion != null ? descripcion.trim() : ""
                );
                polizaRepository.save(poliza);
            }

            return "[ÉXITO]: Póliza " + claveNormalizada + " registrada para el cliente " + curpNormalizada + ".";
        } catch (Exception ex) {
            return "[EX-DND]: No fue posible registrar la póliza en el Sistema Dueño (" + ex.getMessage() + ").";
        }
    }

    public String modificarPoliza(String clave, double monto, String descripcion) {
        return modificarPoliza(clave, null, monto, descripcion);
    }

    public String modificarPoliza(String clave, Integer tipo, double monto, String descripcion) {
        try {
            if (clave == null || clave.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: La clave de la póliza es obligatoria (y no puede modificarse).";
            }
            if (monto <= 0) {
                return "[ERROR DE VALIDACIÓN]: El monto asegurado debe ser mayor a 0.";
            }

            String claveNormalizada = clave.trim();
            if (polizaRepository != null) {
                Optional<Poliza> encontrada = polizaRepository.findById(claveNormalizada);
                if (encontrada.isEmpty()) {
                    return "[ALERTA]: No se encontró la póliza con clave " + claveNormalizada + ".";
                }
                Poliza poliza = encontrada.get();
                if (tipo != null) {
                    poliza.setTipo(tipo);
                }
                poliza.setMonto(monto);
                if (descripcion != null) {
                    poliza.setDescripcion(descripcion.trim());
                }
                polizaRepository.save(poliza);
            }

            return "[ÉXITO]: Póliza " + claveNormalizada + " modificada correctamente.";
        } catch (Exception ex) {
            return "[EX-DND]: No fue posible modificar la póliza (" + ex.getMessage() + ").";
        }
    }

    public String darDeBajaPoliza(String clave) {
        try {
            if (clave == null || clave.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: Debe indicar la clave de la póliza a dar de baja.";
            }

            String claveNormalizada = clave.trim();
            if (polizaRepository != null) {
                Optional<Poliza> encontrada = polizaRepository.findById(claveNormalizada);
                if (encontrada.isEmpty()) {
                    return "[ALERTA]: No se encontró la póliza con clave " + claveNormalizada + ".";
                }
                polizaRepository.deleteById(claveNormalizada);
            }

            return "[ÉXITO]: Póliza " + claveNormalizada + " dada de baja correctamente.";
        } catch (Exception ex) {
            return "[EX-DND]: No fue posible dar de baja la póliza (" + ex.getMessage() + ").";
        }
    }
}