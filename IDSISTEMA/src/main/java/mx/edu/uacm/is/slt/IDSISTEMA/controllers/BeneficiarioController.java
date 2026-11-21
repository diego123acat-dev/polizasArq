package mx.edu.uacm.is.slt.IDSISTEMA.controllers;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/beneficiarios")
public class BeneficiarioController {

    @PostMapping("/registrar")
    public String registrarBeneficiario(
            @RequestParam String curp,
            @RequestParam String nombres,
            @RequestParam String primerApellido,
            @RequestParam String segundoApellido,
            @RequestParam String direccion,
            @RequestParam LocalDate fechaNacimiento) {
        
        return "Simulación: Beneficiario " + nombres + " " + primerApellido + " con CURP " + curp + " registrado correctamente.";
    }

    @DeleteMapping("/eliminar/{curp}")
    public String eliminarBeneficiario(
            @PathVariable String curp) {
        
        return "Simulación: Beneficiario con CURP " + curp + " eliminado correctamente.";
    }
}