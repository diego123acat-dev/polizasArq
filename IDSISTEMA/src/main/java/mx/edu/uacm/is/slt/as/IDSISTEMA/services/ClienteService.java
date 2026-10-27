package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import models.Cliente;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClienteService {

    // Simulamos una "base de datos" en memoria usando un Mapa
    private final Map<String, Cliente> baseDatosClientes = new HashMap<>();

    public String registrarCliente(String curp, String nombres, String apellidos, String direccion, Date fechaNacimiento) {
        if (baseDatosClientes.containsKey(curp)) {
            return "Error: El cliente con CURP " + curp + " ya está registrado.";
        }
        
        Cliente nuevoCliente = new Cliente(curp, nombres, apellidos, direccion, fechaNacimiento);
        baseDatosClientes.put(curp, nuevoCliente);
        
        // Aquí en un futuro se haría la petición POST al Sistema Dueño
        return "Cliente " + nombres + " registrado exitosamente en el sistema local.";
    }

    public String modificarCliente(String curp, String nuevaDireccion) {
        if (!baseDatosClientes.containsKey(curp)) {
            return "Error: Cliente no encontrado.";
        }
        
        Cliente cliente = baseDatosClientes.get(curp);
        cliente.setDireccion(nuevaDireccion);
        
        return "Dirección del cliente " + curp + " actualizada a: " + nuevaDireccion;
    }

    public Cliente consultarCliente(String curp) {
        // Para el GET, la práctica pide consultar al Sistema Dueño, pero por ahora lo sacamos de memoria
        return baseDatosClientes.get(curp);
    }
}
