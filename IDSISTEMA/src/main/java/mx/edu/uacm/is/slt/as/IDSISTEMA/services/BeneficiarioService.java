package mx.edu.uacm.is.slt.as.IDSISTEMA.services;


import models.Beneficiario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BeneficiarioService {

    // Guardamos los beneficiarios asociados a una clave de póliza
    private final Map<String, List<Beneficiario>> baseDatosBeneficiarios = new HashMap<>();

    @Autowired
    private PolizaService polizaService;

    public String registrarBeneficiario(String clavePoliza, String nombres, String apellidos, int porcentaje) {
        // Validar que la póliza exista
        if (polizaService.consultarPoliza(clavePoliza) == null) {
            return "Error: La póliza " + clavePoliza + " no existe.";
        }

        Beneficiario nuevoBeneficiario = new Beneficiario(clavePoliza, nombres, apellidos, porcentaje);
        
        baseDatosBeneficiarios.putIfAbsent(clavePoliza, new ArrayList<>());
        
        // Validar que los porcentajes no sumen más de 100%
        int sumaActual = 0;
        for (Beneficiario b : baseDatosBeneficiarios.get(clavePoliza)) {
            sumaActual += b.getPorcentaje();
        }
        
        if (sumaActual + porcentaje > 100) {
            return "Error: La suma de porcentajes de los beneficiarios superaría el 100%.";
        }

        baseDatosBeneficiarios.get(clavePoliza).add(nuevoBeneficiario);
        return "Beneficiario " + nombres + " agregado a la póliza " + clavePoliza;
    }

    public List<Beneficiario> consultarBeneficiarios(String clavePoliza) {
        return baseDatosBeneficiarios.getOrDefault(clavePoliza, new ArrayList<>());
    }

    public void eliminarBeneficiario(String clavePoliza, String nombreCompleto) {
        if (baseDatosBeneficiarios.containsKey(clavePoliza)) {
            baseDatosBeneficiarios.get(clavePoliza).removeIf(b -> b.getNombreCompleto().equalsIgnoreCase(nombreCompleto));
        }
    }
}