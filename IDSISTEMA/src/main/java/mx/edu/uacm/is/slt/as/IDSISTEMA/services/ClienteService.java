package mx.edu.uacm.is.slt.as.IDSISTEMA.services;

import mx.edu.uacm.is.slt.as.IDSISTEMA.Cliente; // Importación correcta de tu modelo
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClienteService {

    // Simulamos una "base de datos" en memoria usando un Mapa
    private final Map<String, Cliente> baseDatosClientes = new HashMap<>();

    public String registrarCliente(String curp, String nombres, String primerApellido, String segundoApellido, String direccion, Date fechaNacimiento) {
        if (baseDatosClientes.containsKey(curp)) {
            return "Error: El cliente con CURP " + curp + " ya está registrado.";
        }
        
        // Usamos el constructor con los atributos exactos de tu entidad Cliente.java
        Cliente nuevoCliente = new Cliente(curp, nombres, primerApellido, segundoApellido, direccion, fechaNacimiento);
        baseDatosClientes.put(curp, nuevoCliente);
        
        // Aquí en un futuro se haría la petición al Sistema Dueño
        return "Cliente " + nombres + " " + primerApellido + " registrado exitosamente en el sistema local.";
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