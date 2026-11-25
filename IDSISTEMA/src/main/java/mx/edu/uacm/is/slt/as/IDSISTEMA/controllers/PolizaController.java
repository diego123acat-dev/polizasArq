package mx.edu.uacm.is.slt.as.IDSISTEMA.controllers;

import mx.edu.uacm.is.slt.as.IDSISTEMA.services.PolizaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping("/consultar/{clave}")
    public String consultarPoliza(@PathVariable String clave) {
        return polizaService.consultarPoliza(clave);
    }

    @GetMapping("/cliente/{curpCliente}")
    public String consultarPolizasPorCliente(@PathVariable String curpCliente) {
        return polizaService.consultarPolizasPorCliente(curpCliente);
    }

    @PostMapping("/registrar")
    public String registrarPoliza(
            @RequestParam String clave,
            @RequestParam int tipo,
            @RequestParam double monto,
            @RequestParam String descripcion,
            @RequestParam String curpCliente) {

        return polizaService.registrarPoliza(clave, tipo, monto, descripcion, curpCliente);
    }

    @PutMapping("/modificar/{clave}")
    public String modificarPoliza(
            @PathVariable String clave,
            @RequestParam double monto,
            @RequestParam String descripcion) {

        return polizaService.modificarPoliza(clave, monto, descripcion);
    }

    @DeleteMapping("/baja/{clave}")
    public String darDeBajaPoliza(@PathVariable String clave) {
        return polizaService.darDeBajaPoliza(clave);
    }
}