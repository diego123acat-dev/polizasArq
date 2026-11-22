 package mx.edu.uacm.is.slt.as.IDSISTEMA.controllers;

 import mx.edu.uacm.is.slt.as.IDSISTEMA.Beneficiario;
 import mx.edu.uacm.is.slt.as.IDSISTEMA.Cliente;
 import mx.edu.uacm.is.slt.as.IDSISTEMA.services.BeneficiarioService;
 import mx.edu.uacm.is.slt.as.IDSISTEMA.services.ClienteService;
 import mx.edu.uacm.is.slt.as.IDSISTEMA.services.PolizaService;
 import org.springframework.boot.CommandLineRunner;
 import org.springframework.boot.SpringApplication;
 import org.springframework.context.ApplicationContext;
 import org.springframework.stereotype.Component;

 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.time.LocalDate;
 import java.time.format.DateTimeParseException;
 import java.util.Date;
 import java.util.List;
 import java.util.Scanner;

 @Component
 public class VistasController implements CommandLineRunner {

     private final ClienteService clienteService;
     private final PolizaService polizaService;
     private final BeneficiarioService beneficiarioService;
     private final ApplicationContext applicationContext;

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
             System.out.println("        SISTEMA INTERMEDIARIO DE PÓLIZAS - MENÚ PRINCIPAL       ");
             System.out.println("==============================================================");
             System.out.println("[1] Gestión de Clientes");
             System.out.println("[2] Gestión de Pólizas");
             System.out.println("[3] Gestión de Beneficiarios");
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
     // MÓDULO 1: GESTIÓN DE CLIENTES (NRM-AC y NRM-MC)
     // =========================================================================
     private void menuClientes(Scanner scanner) {
         boolean volver = false;

         while (!volver) {
             System.out.println("\n--------------------------------------------------------------");
             System.out.println("                   MÓDULO: GESTIÓN DE CLIENTES                 ");
             System.out.println("--------------------------------------------------------------");
             System.out.println("[1] Registrar Nuevo Cliente (NRM-AC: Alta de Cliente)");
             System.out.println("[2] Modificar Datos de Cliente (NRM-MC: Modificación de Cliente)");
             System.out.println("[3] Consultar Cliente por CURP");
             System.out.println("[0] Regresar al Menú Principal");
             System.out.println("--------------------------------------------------------------");
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
         System.out.println("\n--- REGISTRO DE NUEVO CLIENTE (Escriba 'CANCELAR' para abortar) ---");
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

         String resultado = clienteService.registrarCliente(
                 curp, nombres, primerApellido, segundoApellido, direccion, fechaNacimiento
         );
         mostrarResultado(resultado);
     }

     private void modificarClienteCLI(Scanner scanner) {
         System.out.println("\n--- MODIFICACIÓN DE CLIENTE (Escriba 'CANCELAR' para abortar) ---");
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
         System.out.print("\nIngrese la CURP del cliente a consultar: ");
         String curp = scanner.nextLine().trim();
         Cliente cliente = clienteService.consultarCliente(curp);
         if (cliente == null) {
             mostrarAlerta("No se encontró ningún cliente registrado con CURP: " + curp);
         } else {
             System.out.println("\n[CLIENTE ENCONTRADO]");
             System.out.println(cliente);
         }
     }

     // =========================================================================
     // MÓDULO 2: GESTIÓN DE PÓLIZAS (NRM-AP, NRM-CP, NRM-MP y NRM-BP)
     // =========================================================================
     private void menuPolizas(Scanner scanner) {
         boolean volver = false;

         while (!volver) {
             System.out.println("\n--------------------------------------------------------------");
             System.out.println("                    MÓDULO: GESTIÓN DE PÓLIZAS                ");
             System.out.println("--------------------------------------------------------------");
             System.out.println("[1] Registrar Póliza (NRM-AP: Alta de Póliza)");
             System.out.println("[2] Consultar Póliza en Línea (NRM-CP: Verbo GET)");
             System.out.println("[3] Modificar Póliza (NRM-MP)");
             System.out.println("[4] Dar de Baja Póliza (NRM-BP)");
             System.out.println("[5] Consultar Pólizas por Cliente");
             System.out.println("[0] Regresar al Menú Principal");
             System.out.println("--------------------------------------------------------------");
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
         System.out.println("\n--- REGISTRO DE PÓLIZA (Escriba 'CANCELAR' para abortar) ---");
         System.out.print("Ingrese la Clave de la Póliza: ");
         String clave = scanner.nextLine().trim();
         if (esCancelacion(clave)) return;

         System.out.print("Ingrese el Tipo de Póliza (número entero, ej. 1): ");
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
             String resultado = polizaService.registrarPoliza(clave, tipo, monto, descripcion, curpCliente);
             mostrarResultado(resultado);
         } catch (NumberFormatException e) {
             mostrarAlerta("Tipo y monto deben ser valores numéricos válidos.");
         }
     }

     private void consultarPolizaCLI(Scanner scanner) {
         System.out.println("\n--- CONSULTA DE PÓLIZA ---");
         System.out.print("Ingrese la Clave de Póliza: ");
         String clave = scanner.nextLine().trim();
         if (esCancelacion(clave)) return;

         System.out.println("[PROCESANDO] Conectando con el Sistema Dueño de las Pólizas...");
         String resultado = polizaService.consultarPoliza(clave);
         if (resultado == null || resultado.isBlank()) {
             mostrarAlerta("No se obtuvo información para la póliza " + clave + ".");
         } else if (resultado.startsWith("[EX-DND]")) {
             mostrarErrorCritico(resultado);
         } else {
             System.out.println("\n[RESPUESTA RECIBIDA]");
             System.out.println(resultado);
         }
     }

     private void modificarPolizaCLI(Scanner scanner) {
         System.out.println("\n--- MODIFICACIÓN DE PÓLIZA (Escriba 'CANCELAR' para abortar) ---");
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
             String resultado = polizaService.modificarPoliza(clave, tipo, monto, descripcion);
             mostrarResultado(resultado);
         } catch (NumberFormatException e) {
             mostrarAlerta("El tipo y el monto deben ser valores numéricos válidos.");
         }
     }

     private void darDeBajaPolizaCLI(Scanner scanner) {
         System.out.println("\n--- BAJA DE PÓLIZA (Escriba 'CANCELAR' para abortar) ---");
         System.out.print("Ingrese la Clave de la Póliza a dar de baja: ");
         String clave = scanner.nextLine().trim();
         if (esCancelacion(clave)) return;

         System.out.print("¿Confirma dar de baja la póliza " + clave + "? (S/N): ");
         String confirmacion = scanner.nextLine().trim();
         if (!confirmacion.equalsIgnoreCase("S")) {
             System.out.println("Operación de baja abortada por el usuario.");
             return;
         }

         String resultado = polizaService.darDeBajaPoliza(clave);
         mostrarResultado(resultado);
     }

     private void consultarPolizasPorClienteCLI(Scanner scanner) {
         System.out.print("\nIngrese la CURP del Cliente: ");
         String curp = scanner.nextLine().trim();
         if (esCancelacion(curp)) return;

         System.out.println("[PROCESANDO] Consultando pólizas del cliente en el Sistema Dueño...");
         String resultado = polizaService.consultarPolizasPorCliente(curp);
         if (resultado == null || resultado.isBlank()) {
             mostrarAlerta("No se encontraron pólizas para el cliente " + curp + ".");
         } else if (resultado.startsWith("[EX-DND]")) {
             mostrarErrorCritico(resultado);
         } else {
             System.out.println("\n[PÓLIZAS DEL CLIENTE]");
             System.out.println(resultado);
         }
     }

     // =========================================================================
     // MÓDULO 3: GESTIÓN DE BENEFICIARIOS (NRM-AB y NRM-CB)
     // =========================================================================
     private void menuBeneficiarios(Scanner scanner) {
         boolean volver = false;

         while (!volver) {
             System.out.println("\n--------------------------------------------------------------");
             System.out.println("                  MÓDULO: GESTIÓN DE BENEFICIARIOS            ");
             System.out.println("--------------------------------------------------------------");
             System.out.println("[1] Registrar Beneficiario (NRM-AB: Alta de Beneficiario)");
             System.out.println("[2] Consultar Beneficiarios por Póliza (NRM-CB)");
             System.out.println("[3] Eliminar Beneficiario");
             System.out.println("[0] Regresar al Menú Principal");
             System.out.println("--------------------------------------------------------------");
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
         System.out.println("\n--- REGISTRO DE BENEFICIARIO (Escriba 'CANCELAR' para abortar) ---");
         System.out.print("Ingrese la Clave de la Póliza: ");
         String clavePoliza = scanner.nextLine().trim();
         if (esCancelacion(clavePoliza)) return;

         System.out.print("Ingrese la CURP del Beneficiario: ");
         String curp = scanner.nextLine().trim();
         if (esCancelacion(curp)) return;

         System.out.print("Ingrese Nombres del Beneficiario: ");
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
         System.out.println("\n--- CONSULTA DE BENEFICIARIOS POR PÓLIZA ---");
         System.out.print("Ingrese la Clave de la Póliza: ");
         String clavePoliza = scanner.nextLine().trim();
         if (esCancelacion(clavePoliza)) return;

         List<Beneficiario> lista = beneficiarioService.consultarBeneficiariosPorPoliza(clavePoliza);
         if (lista.isEmpty()) {
             mostrarAlerta("No hay beneficiarios registrados para la póliza " + clavePoliza + ".");
         } else {
             System.out.println("\n[BENEFICIARIOS DE LA PÓLIZA " + clavePoliza + "]");
             for (Beneficiario b : lista) {
                 System.out.println("- CURP: " + b.getCurp()
                         + " | Nombre: " + b.getNombres() + " " + b.getPrimerApellido() + " " + (b.getSegundoApellido() != null ? b.getSegundoApellido() : "")
                         + " | Nacimiento: " + b.getFechaNacimiento()
                         + " | Porcentaje: " + b.getPorcentaje() + "%");
             }
         }
     }

     private void eliminarBeneficiarioCLI(Scanner scanner) {
         System.out.print("\nIngrese la CURP del Beneficiario a eliminar: ");
         String curp = scanner.nextLine().trim();
         if (esCancelacion(curp)) return;

         String resultado = beneficiarioService.eliminarBeneficiario(curp);
         mostrarResultado(resultado);
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
         if (mensaje.startsWith("[EX-DND]")) {
             mostrarErrorCritico(mensaje);
         } else if (mensaje.startsWith("[ALERTA]") || mensaje.startsWith("[ERROR")
                 || mensaje.startsWith("[EX-")) {
             mostrarAlerta(mensaje);
         } else {
             System.out.println("\n" + mensaje);
         }
     }

     private void mostrarAlerta(String mensaje) {
         System.out.println("\n==============================================================");
         System.out.println(mensaje);
         System.out.println("==============================================================");
     }
//-----
     private void mostrarErrorCritico(String mensaje) {
         System.out.println("\n==============================================================");
         System.out.println("[ERROR CRÍTICO]: " + mensaje);
         System.out.println("==============================================================");
     }
 }