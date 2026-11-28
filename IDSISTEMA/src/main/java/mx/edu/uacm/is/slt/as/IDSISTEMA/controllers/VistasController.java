package mx.edu.uacm.is.slt.as.IDSISTEMA.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.edu.uacm.is.slt.as.IDSISTEMA.Beneficiario;
import mx.edu.uacm.is.slt.as.IDSISTEMA.Cliente;
import mx.edu.uacm.is.slt.as.IDSISTEMA.services.BeneficiarioService;
import mx.edu.uacm.is.slt.as.IDSISTEMA.services.ClienteService;
import mx.edu.uacm.is.slt.as.IDSISTEMA.services.PolizaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Component
public class VistasController implements CommandLineRunner {

    private final ClienteService clienteService;
    private final PolizaService polizaService;
    private final BeneficiarioService beneficiarioService;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public VistasController(ClienteService clienteService,
                            PolizaService polizaService,
                            BeneficiarioService beneficiarioService,
                            ApplicationContext applicationContext) {
        this.clienteService = clienteService;
        this.polizaService = polizaService;
        this.beneficiarioService = beneficiarioService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        Thread cliThread = new Thread(this::iniciarMenuPrincipal, "cli-menu-thread");
        cliThread.setDaemon(false);
        cliThread.start();
    }

    private void iniciarMenuPrincipal() {
        Scanner scanner = new Scanner(System.in);
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n==============================================================");
            System.out.println("SISTEMA INTERMEDIARIO DE PÓLIZAS - MENÚ PRINCIPAL");
            System.out.println("==============================================================");
            System.out.println("[1] Gestión de Clientes        ");
            System.out.println("[2] Gestión de Pólizas        ");
            System.out.println("[3] Gestión de Beneficiarios  ");
            System.out.println("[0] Salir / Apagar el Sistema");
            System.out.println("==============================================================");
            System.out.print("Por favor, ingrese el número de la opción deseada: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> menuClientes(scanner);
                case "2" -> menuPolizas(scanner);
                case "3" -> menuBeneficiarios(scanner);
                case "0" -> {
                    System.out.println("\nCerrando Sistema Intermediario de Pólizas...");
                    ejecutando = false;
                    SpringApplication.exit(applicationContext, () -> 0);
                }
                default -> mostrarAlerta("Opción no válida. Seleccione una opción del menú.");
            }
        }
    }

    // =========================================================================
    // MÓDULO 1: GESTIÓN DE CLIENTES (ClienteController)
    // =========================================================================
    private void menuClientes(Scanner scanner) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\nMÓDULO: GESTIÓN DE CLIENTES");
            System.out.println("[1] Registrar Nuevo Cliente");
            System.out.println("[2] Modificar Datos de Cliente");
            System.out.println("[3] Consultar Cliente");
            System.out.println("[0] Regresar al Menú Principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> registrarClienteCLI(scanner);
                case "2" -> modificarClienteCLI(scanner);
                case "3" -> consultarClienteCLI(scanner);
                case "0" -> volver = true;
                default -> mostrarAlerta("Opción no válida en Gestión de Clientes.");
            }
        }
    }

    private void registrarClienteCLI(Scanner scanner) {
        System.out.println("\nREGISTRO DE NUEVO CLIENTE --- (Escriba 'CANCELAR' para abortar)");
        System.out.print("Ingrese la CURP / Identificación: ");
        String curp = scanner.nextLine().trim();
        if (esCancelacion(curp)) return;

        System.out.print("Ingrese Nombres: ");
        String nombres = scanner.nextLine().trim();
        if (esCancelacion(nombres)) return;

        System.out.print("Ingrese Primer Apellido: ");
        String primerApellido = scanner.nextLine().trim();
        if (esCancelacion(primerApellido)) return;

        System.out.print("Ingrese Segundo Apellido (opcional): ");
        String segundoApellido = scanner.nextLine().trim();
        if (esCancelacion(segundoApellido)) return;

        System.out.print("Ingrese Dirección: ");
        String direccion = scanner.nextLine().trim();
        if (esCancelacion(direccion)) return;

        System.out.print("Ingrese Fecha de Nacimiento (yyyy-MM-dd): ");
        String fechaTexto = scanner.nextLine().trim();
        if (esCancelacion(fechaTexto)) return;

        Date fechaNacimiento = parsearFecha(fechaTexto);
        if (fechaNacimiento == null) {
            mostrarAlerta("Formato de fecha inválido. Use yyyy-MM-dd (ejemplo: 1995-08-20).");
            return;
        }

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        String resultado = clienteService.registrarCliente(
                curp, nombres, primerApellido, segundoApellido, direccion, fechaNacimiento
        );
        mostrarResultado(resultado);
    }

    private void modificarClienteCLI(Scanner scanner) {
        System.out.println("\nMODIFICACIÓN DE CLIENTE --- (Escriba 'CANCELAR' para abortar)");
        System.out.print("Ingrese la CURP del cliente a modificar: ");
        String curp = scanner.nextLine().trim();
        if (esCancelacion(curp)) return;

        System.out.print("Nuevos Nombres (Enter para conservar actual): ");
        String nombres = scanner.nextLine().trim();
        if (esCancelacion(nombres)) return;

        System.out.print("Nuevo Primer Apellido (Enter para conservar actual): ");
        String primerApellido = scanner.nextLine().trim();
        if (esCancelacion(primerApellido)) return;

        System.out.print("Nuevo Segundo Apellido (Enter para conservar actual): ");
        String segundoApellido = scanner.nextLine().trim();
        if (esCancelacion(segundoApellido)) return;

        System.out.print("Nueva Dirección (Enter para conservar actual): ");
        String direccion = scanner.nextLine().trim();
        if (esCancelacion(direccion)) return;

        System.out.print("Nueva Fecha de Nacimiento yyyy-MM-dd (Enter para conservar actual): ");
        String fechaTexto = scanner.nextLine().trim();
        if (esCancelacion(fechaTexto)) return;

        Date fechaNacimiento = null;
        if (!fechaTexto.isBlank()) {
            fechaNacimiento = parsearFecha(fechaTexto);
            if (fechaNacimiento == null) {
                mostrarAlerta("Formato de fecha inválido. Use yyyy-MM-dd.");
                return;
            }
        }

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        String resultado = clienteService.modificarCliente(
                curp,
                nombres.isBlank() ? null : nombres,
                primerApellido.isBlank() ? null : primerApellido,
                segundoApellido.isBlank() ? null : segundoApellido,
                direccion.isBlank() ? null : direccion,
                fechaNacimiento
        );
        mostrarResultado(resultado);
    }

    private void consultarClienteCLI(Scanner scanner) {
        System.out.println("\nCONSULTA DE CLIENTE ---");
        System.out.print("Ingrese la CURP / Identificación: ");
        String curp = scanner.nextLine().trim();
        if (esCancelacion(curp)) return;

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        Cliente cliente = clienteService.consultarCliente(curp);
        if (cliente == null) {
            mostrarAlerta("El cliente especificado no existe en el sistema (CURP: " + curp.toUpperCase() + ").");
        } else {
            imprimirDetalleClienteFormateado(cliente);
        }
    }

    // =========================================================================
    // MÓDULO 2: GESTIÓN DE PÓLIZAS (PolizaController)
    // =========================================================================
    private void menuPolizas(Scanner scanner) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\nMÓDULO: GESTIÓN DE PÓLIZAS");
            System.out.println("[1] Registrar Póliza");
            System.out.println("[2] Consultar Póliza en Línea");
            System.out.println("[3] Modificar Póliza");
            System.out.println("[4] Dar de Baja Póliza");
            System.out.println("[5] Consultar Pólizas por Cliente");
            System.out.println("[0] Regresar al Menú Principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> registrarPolizaCLI(scanner);
                case "2" -> consultarPolizaCLI(scanner);
                case "3" -> modificarPolizaCLI(scanner);
                case "4" -> darDeBajaPolizaCLI(scanner);
                case "5" -> consultarPolizasPorClienteCLI(scanner);
                case "0" -> volver = true;
                default -> mostrarAlerta("Opción no válida en Gestión de Pólizas.");
            }
        }
    }

    private void registrarPolizaCLI(Scanner scanner) {
        System.out.println("\nREGISTRO DE PÓLIZA --- (Escriba 'CANCELAR' para abortar)");
        System.out.print("Ingrese la Clave de la Póliza: ");
        String clave = scanner.nextLine().trim();
        if (esCancelacion(clave)) return;

        System.out.print("Ingrese el Tipo de Póliza (número entero,(0,1,2): ");
        String tipoTexto = scanner.nextLine().trim();
        if (esCancelacion(tipoTexto)) return;

        System.out.print("Ingrese el Monto Asegurado: ");
        String montoTexto = scanner.nextLine().trim();
        if (esCancelacion(montoTexto)) return;

        System.out.print("Ingrese la Descripción del Seguro: ");
        String descripcion = scanner.nextLine().trim();
        if (esCancelacion(descripcion)) return;

        System.out.print("Ingrese la CURP del Cliente Asegurado: ");
        String curpCliente = scanner.nextLine().trim();
        if (esCancelacion(curpCliente)) return;

        try {
            int tipo = Integer.parseInt(tipoTexto);
            double monto = Double.parseDouble(montoTexto);
            System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
            String resultado = polizaService.registrarPoliza(clave, tipo, monto, descripcion, curpCliente);
            mostrarResultado(resultado);
        } catch (NumberFormatException e) {
            mostrarAlerta("Tipo y monto deben ser valores numéricos válidos.");
        }
    }

    private void consultarPolizaCLI(Scanner scanner) {
        System.out.println("\nCONSULTA DE PÓLIZA ---");
        System.out.print("Ingrese la Clave de Póliza: ");
        String clave = scanner.nextLine().trim();
        if (esCancelacion(clave)) return;

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        String resultado = polizaService.consultarPoliza(clave);
        if (resultado == null || resultado.isBlank()) {
            mostrarAlerta("No se obtuvo información para la póliza " + clave + ".");
        } else if (resultado.startsWith("[EX-DND]")) {
            mostrarErrorCritico(resultado);
        } else if (resultado.startsWith("[ALERTA]")) {
            mostrarAlerta(resultado);
        } else {
            System.out.println("[RESPUESTA RECIBIDA]");
            imprimirDetallePolizaFormateado(resultado, clave);
        }
    }

    private void modificarPolizaCLI(Scanner scanner) {
        System.out.println("\nMODIFICACIÓN DE PÓLIZA --- (Escriba 'CANCELAR' para abortar)");
        System.out.print("Ingrese la Clave de la Póliza (no modificable): ");
        String clave = scanner.nextLine().trim();
        if (esCancelacion(clave)) return;

        System.out.print("Ingrese el Nuevo Tipo de Póliza (Enter para omitir): ");
        String tipoTexto = scanner.nextLine().trim();
        if (esCancelacion(tipoTexto)) return;

        System.out.print("Ingrese el Nuevo Monto Asegurado: ");
        String montoTexto = scanner.nextLine().trim();
        if (esCancelacion(montoTexto)) return;

        System.out.print("Ingrese la Nueva Descripción: ");
        String descripcion = scanner.nextLine().trim();
        if (esCancelacion(descripcion)) return;

        try {
            Integer tipo = tipoTexto.isBlank() ? null : Integer.parseInt(tipoTexto);
            double monto = Double.parseDouble(montoTexto);
            System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
            String resultado = polizaService.modificarPoliza(clave, tipo, monto, descripcion);
            mostrarResultado(resultado);
        } catch (NumberFormatException e) {
            mostrarAlerta("El tipo y el monto deben ser valores numéricos válidos.");
        }
    }

    private void darDeBajaPolizaCLI(Scanner scanner) {
        System.out.println("\nBAJA DE PÓLIZA --- (Escriba 'CANCELAR' para abortar)");
        System.out.print("Ingrese la Clave de la Póliza a dar de baja: ");
        String clave = scanner.nextLine().trim();
        if (esCancelacion(clave)) return;

        System.out.print("¿Confirma dar de baja la póliza " + clave + "? (S/N): ");
        String confirmacion = scanner.nextLine().trim();
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación de baja abortada por el usuario.");
            return;
        }

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        String resultado = polizaService.darDeBajaPoliza(clave);
        mostrarResultado(resultado);
    }

    private void consultarPolizasPorClienteCLI(Scanner scanner) {
        System.out.println("\nCONSULTA DE PÓLIZAS POR CLIENTE ---");
        System.out.print("Ingrese la CURP / Identificación: ");
        String curp = scanner.nextLine().trim();
        if (esCancelacion(curp)) return;

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        String resultado = polizaService.consultarPolizasPorCliente(curp);
        if (resultado == null || resultado.isBlank()) {
            mostrarAlerta("No se encontraron pólizas para el cliente " + curp.toUpperCase() + ".");
        } else if (resultado.startsWith("[EX-DND]")) {
            mostrarErrorCritico(resultado);
        } else if (resultado.startsWith("[ALERTA]")) {
            mostrarAlerta(resultado);
        } else {
            System.out.println("[RESPUESTA RECIBIDA]");
            imprimirListaPolizasFormateada(resultado);
        }
    }

    // =========================================================================
    // MÓDULO 3: GESTIÓN DE BENEFICIARIOS (BeneficiarioController)
    // =========================================================================
    private void menuBeneficiarios(Scanner scanner) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\nMÓDULO: GESTIÓN DE BENEFICIARIOS");
            System.out.println("[1] Registrar Beneficiario");
            System.out.println("[2] Consultar Beneficiarios por Póliza");
            System.out.println("[3] Eliminar Beneficiario");
            System.out.println("[0] Regresar al Menú Principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> registrarBeneficiarioCLI(scanner);
                case "2" -> consultarBeneficiariosPorPolizaCLI(scanner);
                case "3" -> eliminarBeneficiarioCLI(scanner);
                case "0" -> volver = true;
                default -> mostrarAlerta("Opción no válida en Gestión de Beneficiarios.");
            }
        }
    }

    private void registrarBeneficiarioCLI(Scanner scanner) {
        System.out.println("\nREGISTRO DE BENEFICIARIO --- (Escriba 'CANCELAR' para abortar)");
        System.out.print("Ingrese la Clave de Póliza: ");
        String clavePoliza = scanner.nextLine().trim();
        if (esCancelacion(clavePoliza)) return;

        System.out.print("Ingrese la CURP / Identificación: ");
        String curp = scanner.nextLine().trim();
        if (esCancelacion(curp)) return;

        System.out.print("Ingrese Nombres: ");
        String nombres = scanner.nextLine().trim();
        if (esCancelacion(nombres)) return;

        System.out.print("Ingrese Primer Apellido: ");
        String primerApellido = scanner.nextLine().trim();
        if (esCancelacion(primerApellido)) return;

        System.out.print("Ingrese Segundo Apellido (opcional): ");
        String segundoApellido = scanner.nextLine().trim();
        if (esCancelacion(segundoApellido)) return;

        System.out.print("Ingrese Dirección (opcional): ");
        String direccion = scanner.nextLine().trim();
        if (esCancelacion(direccion)) return;

        System.out.print("Ingrese Fecha de Nacimiento (yyyy-MM-dd): ");
        String fechaTexto = scanner.nextLine().trim();
        if (esCancelacion(fechaTexto)) return;

        System.out.print("Ingrese Porcentaje de Beneficio (1 a 100): ");
        String porcentajeTexto = scanner.nextLine().trim();
        if (esCancelacion(porcentajeTexto)) return;

        try {
            LocalDate fechaNacimiento = LocalDate.parse(fechaTexto);
            double porcentaje = Double.parseDouble(porcentajeTexto);

            System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
            String resultado = beneficiarioService.registrarBeneficiario(
                    clavePoliza, curp, nombres, primerApellido,
                    segundoApellido, direccion, fechaNacimiento, porcentaje
            );
            mostrarResultado(resultado);
        } catch (DateTimeParseException e) {
            mostrarAlerta("Formato de fecha inválido. Use yyyy-MM-dd (ejemplo: 2000-05-14).");
        } catch (NumberFormatException e) {
            mostrarAlerta("El porcentaje debe ser un número válido (ejemplo: 50).");
        }
    }

    private void consultarBeneficiariosPorPolizaCLI(Scanner scanner) {
        System.out.println("\nCONSULTA DE BENEFICIARIOS POR PÓLIZA ---");
        System.out.print("Ingrese la Clave de Póliza: ");
        String clavePoliza = scanner.nextLine().trim();
        if (esCancelacion(clavePoliza)) return;

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        List<Beneficiario> lista = beneficiarioService.consultarBeneficiariosPorPoliza(clavePoliza);
        if (lista.isEmpty()) {
            mostrarAlerta("No hay beneficiarios registrados para la póliza " + clavePoliza + ".");
        } else {
            imprimirListaBeneficiariosFormateada(lista, clavePoliza);
        }
    }

    private void eliminarBeneficiarioCLI(Scanner scanner) {
        System.out.println("\nELIMINACIÓN DE BENEFICIARIO ---");
        System.out.print("Ingrese la CURP / Identificación: ");
        String curp = scanner.nextLine().trim();
        if (esCancelacion(curp)) return;

        System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
        String resultado = beneficiarioService.eliminarBeneficiario(curp);
        mostrarResultado(resultado);
    }

    // =========================================================================
    // FORMATEO UNIFORME DE RESPUESTAS (PROPUESTA DE INTERFAZ DE USUARIO)
    // =========================================================================
    private void imprimirDetalleClienteFormateado(Cliente cliente) {
        String apellidos = cliente.getPrimerApellido()
                + (cliente.getSegundoApellido() != null && !cliente.getSegundoApellido().isBlank()
                ? " " + cliente.getSegundoApellido()
                : "");
        String fecha = cliente.getFechaNacimiento() != null
                ? new SimpleDateFormat("yyyy-MM-dd").format(cliente.getFechaNacimiento())
                : "N/D";
        String direccion = (cliente.getDireccion() != null && !cliente.getDireccion().isBlank())
                ? decodificarTexto(cliente.getDireccion())
                : "N/D";

        System.out.println("[RESPUESTA RECIBIDA]");
        System.out.println("- CURP: " + cliente.getCurp());
        System.out.println("- Nombres: " + decodificarTexto(cliente.getNombres()));
        System.out.println("- Apellidos: " + decodificarTexto(apellidos));
        System.out.println("- Dirección: " + direccion);
        System.out.println("- Fecha de Nacimiento: " + fecha);
    }

    private void imprimirDetallePolizaFormateado(String resultado, String claveFallback) {
        try {
            String jsonCandidato = extraerJson(resultado);
            JsonNode nodo = objectMapper.readTree(jsonCandidato);
            if (nodo.isArray() && nodo.size() > 0) {
                nodo = nodo.get(0);
            }

            if (nodo.isObject()) {
                imprimirNodoPoliza(nodo, claveFallback);
                return;
            }
        } catch (Exception ignored) {
        }
        System.out.println(resultado);
    }

    private void imprimirListaPolizasFormateada(String resultado) {
        try {
            String jsonCandidato = extraerJson(resultado);
            JsonNode nodo = objectMapper.readTree(jsonCandidato);

            if (nodo.isArray()) {
                if (nodo.isEmpty()) {
                    System.out.println("- Sin pólizas registradas.");
                    return;
                }
                for (int i = 0; i < nodo.size(); i++) {
                    if (i > 0) {
                        System.out.println("--------------------------------------------------------------");
                    }
                    imprimirNodoPoliza(nodo.get(i), "N/D");
                }
                return;
            } else if (nodo.isObject()) {
                imprimirNodoPoliza(nodo, "N/D");
                return;
            }
        } catch (Exception ignored) {
        }
        System.out.println(resultado);
    }

    private void imprimirNodoPoliza(JsonNode nodo, String claveFallback) {
        String clave = textoSeguro(nodo, "clave", claveFallback);
        String tipo = textoSeguro(nodo, "tipo", "N/D");
        String descripcion = decodificarTexto(textoSeguro(nodo, "descripcion", ""));
        double montoVal = nodo.has("monto") && nodo.get("monto").isNumber()
                ? nodo.get("monto").asDouble()
                : parseDoubleSeguro(textoSeguro(nodo, "monto", "0"));

        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.US);
        String montoFormateado = formatoMoneda.format(montoVal);

        System.out.println("- Clave: " + clave);
        System.out.println("- Tipo: " + tipo);
        System.out.println("- Monto: " + montoFormateado);
        System.out.println("- Descripción: " + descripcion);
    }

    private void imprimirListaBeneficiariosFormateada(List<Beneficiario> lista, String clavePoliza) {
        System.out.println("[RESPUESTA RECIBIDA]");
        System.out.println("- Clave de Póliza: " + clavePoliza);
        for (int i = 0; i < lista.size(); i++) {
            Beneficiario b = lista.get(i);
            String apellidos = b.getPrimerApellido()
                    + (b.getSegundoApellido() != null && !b.getSegundoApellido().isBlank()
                    ? " " + b.getSegundoApellido()
                    : "");
            String porcentajeTexto = (b.getPorcentaje() % 1 == 0)
                    ? String.valueOf((int) b.getPorcentaje())
                    : String.valueOf(b.getPorcentaje());

            if (i > 0) {
                System.out.println("--------------------------------------------------------------");
            }
            System.out.println("- CURP: " + b.getCurp());
            System.out.println("- Nombres: " + decodificarTexto(b.getNombres()));
            System.out.println("- Apellidos: " + decodificarTexto(apellidos));
            System.out.println("- Fecha de Nacimiento: " + (b.getFechaNacimiento() != null ? b.getFechaNacimiento() : "N/D"));
            System.out.println("- Porcentaje: " + porcentajeTexto + "%");
        }
    }

    private String extraerJson(String texto) {
        String limpio = texto.trim();
        int idxLlave = limpio.indexOf('{');
        int idxCorchete = limpio.indexOf('[');
        if (idxCorchete >= 0 && (idxLlave < 0 || idxCorchete < idxLlave)) {
            return limpio.substring(idxCorchete);
        }
        if (idxLlave >= 0) {
            return limpio.substring(idxLlave);
        }
        return limpio;
    }

    private String textoSeguro(JsonNode nodo, String campo, String defecto) {
        if (nodo.hasNonNull(campo)) {
            String val = nodo.get(campo).asText().trim();
            if (!val.isBlank() && !"null".equalsIgnoreCase(val)) {
                return val;
            }
        }
        return defecto;
    }

    private double parseDoubleSeguro(String texto) {
        try {
            return Double.parseDouble(texto);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String decodificarTexto(String texto) {
        try {
            return URLDecoder.decode(texto, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return texto;
        }
    }

    // =========================================================================
    // UTILIDADES DE VALIDACIÓN Y FORMATO DE NOTIFICACIONES
    // =========================================================================
    private boolean esCancelacion(String entrada) {
        if ("CANCELAR".equalsIgnoreCase(entrada)) {
            System.out.println("Operación cancelada. Regresando al menú anterior...");
            return true;
        }
        return false;
    }

    private Date parsearFecha(String texto) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            return sdf.parse(texto);
        } catch (ParseException e) {
            return null;
        }
    }

    private void mostrarResultado(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            mostrarAlerta("No se recibió respuesta del sistema.");
            return;
        }
        if (mensaje.startsWith("[EX-DND]")) {
            mostrarErrorCritico(mensaje);
        } else if (mensaje.startsWith("[ALERTA]") || mensaje.startsWith("[ERROR")
                || mensaje.startsWith("[EX-")) {
            mostrarAlerta(mensaje);
        } else {
            imprimirRespuestaEnLista(mensaje);
        }
    }

    private void imprimirRespuestaEnLista(String mensaje) {
        String textoLimpio = mensaje.replaceFirst("^\\[ÉXITO\\]:?\\s*", "[ÉXITO] ");
        int idxPipe = textoLimpio.indexOf(" | Sistema Dueño:");
        String encabezado = idxPipe >= 0 ? textoLimpio.substring(0, idxPipe).trim() : textoLimpio.trim();
        String resto = idxPipe >= 0 ? textoLimpio.substring(idxPipe + " | Sistema Dueño:".length()).trim() : "";

        System.out.println("\n" + encabezado);

        if (!resto.isBlank()) {
            String jsonParte = extraerJson(resto);
            try {
                JsonNode nodo = objectMapper.readTree(jsonParte);
                if (nodo.isArray() && !nodo.isEmpty()) {
                    nodo = nodo.get(0);
                }
                if (nodo.isObject()) {
                    System.out.println("[RESPUESTA RECIBIDA]");
                    imprimirCamposNodo(nodo);
                    return;
                }
            } catch (Exception ignored) {
            }
            System.out.println("[RESPUESTA RECIBIDA]");
            System.out.println("- Detalle: " + decodificarTexto(resto));
        }
    }

    private void imprimirCamposNodo(JsonNode nodo) {
        nodo.fields().forEachRemaining(entry -> {
            String campo = entry.getKey();
            JsonNode valorNode = entry.getValue();
            if (valorNode != null && valorNode.isObject()) {
                imprimirCamposNodo(valorNode);
            } else {
                String valor = formatearValorCampo(campo, valorNode);
                System.out.println("- " + formatearNombreCampo(campo) + ": " + valor);
            }
        });
    }

    private String formatearValorCampo(String campo, JsonNode valorNode) {
        boolean esSegundoApellido = campo != null && campo.equalsIgnoreCase("segundoApellido");
        if (valorNode == null || valorNode.isNull()) {
            return esSegundoApellido ? "no tiene" : "";
        }
        String texto = decodificarTexto(valorNode.asText().trim());
        if (texto.isBlank() || "null".equalsIgnoreCase(texto)) {
            return esSegundoApellido ? "no tiene" : "";
        }
        if (campo != null && campo.toLowerCase().contains("fecha") && texto.length() >= 10) {
            return texto.substring(0, 10);
        }
        return texto;
    }

    private String formatearNombreCampo(String campo) {
        if (campo == null || campo.isBlank()) {
            return "Dato";
        }
        String conEspacios = campo.replaceAll("([a-z])([A-Z])", "$1 $2").replace('_', ' ');
        return Character.toUpperCase(conEspacios.charAt(0)) + conEspacios.substring(1);
    }
    private void mostrarAlerta(String mensaje) {
        System.out.println("\n==============================================================");
        System.out.println(mensaje.startsWith("[ALERTA]") ? mensaje : "[ALERTA]: " + mensaje);
        System.out.println("==============================================================");
    }

    private void mostrarErrorCritico(String mensaje) {
        System.out.println("\n==============================================================");
        System.out.println(mensaje.startsWith("[ERROR CRITICO]") ? mensaje : "[ERROR CRITICO]: " + mensaje);
        System.out.println("==============================================================");
    }
}