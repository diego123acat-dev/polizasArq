package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Beneficiario; // Importación correcta de tu modelo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BeneficiarioService {

    // Simulamos una base de datos temporal agrupada por CURP o clave
    private final Map<String, List<Beneficiario>> baseDatosBeneficiarios = new HashMap<>();

    @Autowired
    private PolizaService polizaService;

    public String registrarBeneficiario(String clavePoliza, String curp, String nombres, String primerApellido, String segundoApellido, String direccion) {
        // Validar que la póliza exista (según tu lógica de negocio)
        if (polizaService != null && polizaService.consultarPoliza(clavePoliza) == null) {
            return "Error: La póliza " + clavePoliza + " no existe.";
        }

        // Creamos el beneficiario usando los atributos reales de tu modelo
        Beneficiario nuevoBeneficiario = new Beneficiario(curp, nombres, primerApellido, segundoApellido, direccion, null);
        
        baseDatosBeneficiarios.putIfAbsent(clavePoliza, new ArrayList<>());
        baseDatosBeneficiarios.get(clavePoliza).add(nuevoBeneficiario);

        return "Beneficiario " + nombres + " " + primerApellido + " agregado correctamente a la póliza " + clavePoliza;
    }

    public List<Beneficiario> consultarBeneficiarios(String clavePoliza) {
        return baseDatosBeneficiarios.getOrDefault(clavePoliza, new ArrayList<>());
    }

    public void eliminarBeneficiario(String clavePoliza, String curp) {
        if (baseDatosBeneficiarios.containsKey(clavePoliza)) {
            baseDatosBeneficiarios.get(clavePoliza).removeIf(b -> b.getCurp().equalsIgnoreCase(curp));
        }
    }
}