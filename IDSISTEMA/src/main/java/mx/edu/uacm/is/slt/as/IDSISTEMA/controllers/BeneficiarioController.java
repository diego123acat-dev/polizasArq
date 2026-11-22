package mx.edu.uacm.is.slt.as.IDSISTEMA.controllers;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Beneficiario;
import mx.edu.uacm.is.slt.as.IDSISTEMA.services.BeneficiarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/beneficiarios")
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;

    public BeneficiarioController(BeneficiarioService beneficiarioService) {
        this.beneficiarioService = beneficiarioService;
    }

    @GetMapping("/consultar")
    public List<Beneficiario> consultarBeneficiarios() {
        return beneficiarioService.consultarBeneficiarios();
    }

    @PostMapping("/registrar")
    public String registrarBeneficiario(
            @RequestParam String clavePoliza,
            @RequestParam String curp,
            @RequestParam String nombres,
            @RequestParam String primerApellido,
            @RequestParam(required = false, defaultValue = "") String segundoApellido,
            @RequestParam(required = false, defaultValue = "") String direccion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento) {

        return beneficiarioService.registrarBeneficiario(
                clavePoliza, curp, nombres, primerApellido, segundoApellido, direccion, fechaNacimiento
        );
    }
//---
    @DeleteMapping("/eliminar/{curp}")
    public String eliminarBeneficiario(@PathVariable String curp) {
        return beneficiarioService.eliminarBeneficiario(curp);
    }
}