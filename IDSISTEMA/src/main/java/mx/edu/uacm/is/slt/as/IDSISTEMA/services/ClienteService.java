package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Cliente;
import mx.edu.uacm.is.slt.as.IDSISTEMA.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public String registrarCliente(String curp, String nombres, String primerApellido,
                                   String segundoApellido, String direccion, Date fechaNacimiento) {
        try {
            if (curp == null || curp.isBlank() || nombres == null || nombres.isBlank()
                    || primerApellido == null || primerApellido.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: CURP, nombres y primer apellido son obligatorios.";
            }

            String curpNormalizada = curp.trim().toUpperCase();
            if (clienteRepository.existsById(curpNormalizada)) {
                return "[EX-CYR]: El cliente con CURP " + curpNormalizada + " ya está registrado.";
            }

            Cliente nuevoCliente = new Cliente(
                    curpNormalizada, nombres.trim(), primerApellido.trim(),
                    segundoApellido != null ? segundoApellido.trim() : "",
                    direccion != null ? direccion.trim() : "",
                    fechaNacimiento
            );
            clienteRepository.save(nuevoCliente);
            return "[ÉXITO]: Cliente " + nombres.trim() + " " + primerApellido.trim() + " registrado correctamente.";
        } catch (Exception ex) {
            return "[EX-DND]: No fue posible completar el registro del cliente (" + ex.getMessage() + ").";
        }
    }

    public String modificarCliente(String curp, String nuevaDireccion) {
        return modificarCliente(curp, null, null, null, nuevaDireccion, null);
    }

    public String modificarCliente(String curp, String nombres, String primerApellido,
                                   String segundoApellido, String nuevaDireccion, Date fechaNacimiento) {
        try {
            if (curp == null || curp.isBlank()) {
                return "[ERROR DE VALIDACIÓN]: Debe indicar la CURP del cliente.";
            }

            String curpNormalizada = curp.trim().toUpperCase();
            Optional<Cliente> encontrado = clienteRepository.findById(curpNormalizada);
            if (encontrado.isEmpty()) {
                return "[ALERTA]: No existe un cliente registrado con la CURP " + curpNormalizada + ".";
            }

            Cliente cliente = encontrado.get();
            if (nombres != null && !nombres.isBlank()) {
                cliente.setNombres(nombres.trim());
            }
            if (primerApellido != null && !primerApellido.isBlank()) {
                cliente.setPrimerApellido(primerApellido.trim());
            }
            if (segundoApellido != null) {
                cliente.setSegundoApellido(segundoApellido.trim());
            }
            if (nuevaDireccion != null && !nuevaDireccion.isBlank()) {
                cliente.setDireccion(nuevaDireccion.trim());
            }
            if (fechaNacimiento != null) {
                cliente.setFechaNacimiento(fechaNacimiento);
            }

            clienteRepository.save(cliente);
            return "[ÉXITO]: Datos del cliente " + curpNormalizada + " actualizados correctamente.";
        } catch (Exception ex) {
            return "[EX-DND]: No fue posible modificar al cliente (" + ex.getMessage() + ").";
        }
    }

    public Cliente consultarCliente(String curp) {
        if (curp == null || curp.isBlank()) {
            return null;
        }
        return clienteRepository.findById(curp.trim().toUpperCase()).orElse(null);
    }

    public List<Cliente> consultarClientes() {
        return clienteRepository.findAll();
    }
}