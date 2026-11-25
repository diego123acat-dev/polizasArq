package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Beneficiario;
import mx.edu.uacm.is.slt.as.IDSISTEMA.BeneficiarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final PolizaService polizaService;

    public BeneficiarioService(BeneficiarioRepository beneficiarioRepository,
                               PolizaService polizaService) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.polizaService = polizaService;
    }

    public String registrarBeneficiario(String clavePoliza, String curp, String nombres,
                                         String primerApellido, String segundoApellido,
                                         String direccion, LocalDate fechaNacimiento) {
        return registrarBeneficiario(
                clavePoliza, curp, nombres, primerApellido,
                segundoApellido, direccion, fechaNacimiento, 100.0
        );
    }

    public String registrarBeneficiario(String clavePoliza, String curp, String nombres,
                                         String primerApellido, String segundoApellido,
                                         String direccion, LocalDate fechaNacimiento,
                                         double porcentaje) {
        try {
            if (clavePoliza == null || clavePoliza.isBlank() || curp == null || curp.isBlank()
                    || nombres == null || nombres.isBlank() || primerApellido == null || primerApellido.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: Póliza, CURP, nombres y primer apellido son obligatorios.";
            }
            if (porcentaje <= 0 || porcentaje > 100) {
                return "[ERROR DE VALIDACIÓN]: El porcentaje de beneficio debe ser mayor a 0 y menor o igual al 100 %.";
            }

            String claveNormalizada = clavePoliza.trim();
            String curpNormalizada = curp.trim().toUpperCase();

            try {
                String consultaPoliza = polizaService.consultarPoliza(claveNormalizada);
                if (consultaPoliza == null || consultaPoliza.startsWith("[EX-DND]")) {
                    return "[EX-DND]: No se pudo verificar la póliza " + claveNormalizada + " con el Sistema Dueño.";
                }
            } catch (Exception ex) {
                return "[EX-DND]: El Sistema Dueño no responde para validar la póliza " + claveNormalizada + ".";
            }

            if (beneficiarioRepository.existsByClavePolizaAndCurpIgnoreCase(claveNormalizada, curpNormalizada)) {
                return "[EX-BYR]: El beneficiario con CURP " + curpNormalizada + " ya está registrado en la póliza " + claveNormalizada + ".";
            }

            double sumaActual = beneficiarioRepository.findByClavePoliza(claveNormalizada)
                    .stream()
                    .mapToDouble(Beneficiario::getPorcentaje)
                    .sum();

            if (sumaActual + porcentaje > 100.0) {
                return "[ERROR DE VALIDACIÓN]: La suma de porcentajes (" + (sumaActual + porcentaje)
                        + " %) excedería el 100 % permitido en la póliza " + claveNormalizada + ".";
            }

            Beneficiario nuevoBeneficiario = new Beneficiario(
                    claveNormalizada, curpNormalizada, nombres.trim(), primerApellido.trim(),
                    segundoApellido != null ? segundoApellido.trim() : "",
                    direccion != null ? direccion.trim() : "",
                    fechaNacimiento, porcentaje
            );
            beneficiarioRepository.save(nuevoBeneficiario);

            return "[ÉXITO]: Beneficiario " + nombres.trim() + " " + primerApellido.trim()
                    + " (" + porcentaje + " %) registrado en la póliza " + claveNormalizada + ".";
        } catch (Exception ex) {
            return "[EX-DND]: Error al registrar el beneficiario (" + ex.getMessage() + ").";
        }
    }

    public List<Beneficiario> consultarBeneficiarios() {
        try {
            return beneficiarioRepository.findAll();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<Beneficiario> consultarBeneficiariosPorPoliza(String clavePoliza) {
        try {
            if (clavePoliza == null || clavePoliza.isBlank()) {
                return Collections.emptyList();
            }
            return beneficiarioRepository.findByClavePoliza(clavePoliza.trim());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    @Transactional
    public String eliminarBeneficiario(String curp) {
        try {
            if (curp == null || curp.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: Debe indicar la CURP del beneficiario.";
            }
            String curpNormalizada = curp.trim().toUpperCase();
            if (!beneficiarioRepository.existsByCurpIgnoreCase(curpNormalizada)) {
                return "[ALERTA]: Beneficiario con CURP " + curpNormalizada + " no encontrado.";
            }
            beneficiarioRepository.deleteByCurpIgnoreCase(curpNormalizada);
            return "[ÉXITO]: Beneficiario con CURP " + curpNormalizada + " eliminado correctamente.";
        } catch (Exception ex) {
            return "[EX-DND]: Error al eliminar el beneficiario (" + ex.getMessage() + ").";
        }
    }
}