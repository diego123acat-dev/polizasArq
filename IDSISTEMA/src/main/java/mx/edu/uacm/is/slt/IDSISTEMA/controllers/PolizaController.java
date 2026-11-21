package mx.edu.uacm.is.slt.IDSISTEMA.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {

    @PostMapping("/registrar")
    public String registrarPoliza(
            @RequestParam String clave,
            @RequestParam int tipo,
            @RequestParam double monto,
            @RequestParam String descripcion,
            @RequestParam String curpCliente) {
        
        return "Simulación: Póliza " + clave + " de tipo " + tipo + " registrada para el cliente " + curpCliente;
    }

    @PutMapping("/modificar/{clave}")
    public String modificarPoliza(
            @PathVariable String clave,
            @RequestParam double monto,
            @RequestParam String descripcion) {
        
        return "Simulación: Póliza " + clave + " modificada con nuevo monto: " + monto;
    }

    @DeleteMapping("/baja/{clave}")
    public String darDeBajaPoliza(@PathVariable String clave) {
        return "Simulación: Póliza " + clave + " dada de baja correctamente.";
    }
}