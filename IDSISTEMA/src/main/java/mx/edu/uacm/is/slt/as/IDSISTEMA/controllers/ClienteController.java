package mx.edu.uacm.is.slt.as.IDSISTEMA.controllers;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Cliente;
import mx.edu.uacm.is.slt.as.IDSISTEMA.services.ClienteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/")
    public String inicioClientes() {
        return "Módulo de Clientes activo - Sistema Intermediario de Pólizas.";
    }

    @PostMapping("/registrar")
    public String registrarCliente(
            @RequestParam String curp,
            @RequestParam String nombres,
            @RequestParam String primerApellido,
            @RequestParam(required = false, defaultValue = "") String segundoApellido,
            @RequestParam(required = false, defaultValue = "") String direccion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaNacimiento) {

        return clienteService.registrarCliente(
                curp, nombres, primerApellido, segundoApellido, direccion, fechaNacimiento
        );
    }

    @PutMapping("/modificar/{curp}")
    public String modificarCliente(
            @PathVariable String curp,
            @RequestParam String direccion) {

        return clienteService.modificarCliente(curp, direccion);
    }

    @GetMapping("/consultar/{curp}")
    public Cliente consultarCliente(@PathVariable String curp) {
        return clienteService.consultarCliente(curp);
    }

    @GetMapping("/consultar")
    public List<Cliente> consultarClientes() {
        return clienteService.consultarClientes();
    }
}