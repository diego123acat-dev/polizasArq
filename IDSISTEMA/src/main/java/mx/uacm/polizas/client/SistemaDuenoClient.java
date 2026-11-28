package mx.uacm.polizas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SistemaDuenoClient {

    private final String baseUrl;
    private final RestClient restClient;

    public SistemaDuenoClient(
            @Value("${sistema.dueno.url:http://nachintoch.mx:8080}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private String normalizarSegmento(String valor) {
        if (valor == null || valor.isBlank()) {
            return "null";
        }
        return valor.trim();
    }

    private String normalizarRequerido(String valor) {
        return valor == null ? "" : valor.trim();
    }

    // ==================== GET - PÓLIZAS ====================

    public String consultarPoliza(String clave) {
        return restClient.get()
                .uri("/poliza/{clave}", normalizarRequerido(clave))
                .retrieve()
                .body(String.class);
    }

    public String consultarTodasLasPolizas() {
        return restClient.get()
                .uri("/polizas")
                .retrieve()
                .body(String.class);
    }

    public String consultarPolizasPorCliente(String curpCliente) {
        return restClient.get()
                .uri("/polizas/{curp}", normalizarRequerido(curpCliente).toUpperCase())
                .retrieve()
                .body(String.class);
    }

    public String consultarPolizasPorTipo(int tipo) {
        return restClient.get()
                .uri("/polizas/{tipo}", tipo)
                .retrieve()
                .body(String.class);
    }

    public String consultarPolizasPorNombreCliente(String nombres, String primerApellido, String segundoApellido) {
        return restClient.get()
                .uri("/polizas/c/{nombres}/{primer_apellido}/{segundo_apellido}",
                        normalizarRequerido(nombres),
                        normalizarRequerido(primerApellido),
                        normalizarSegmento(segundoApellido))
                .retrieve()
                .body(String.class);
    }

    public String consultarPolizasPorNombreBeneficiario(String nombres, String primerApellido, String segundoApellido) {
        return restClient.get()
                .uri("/polizas/b/{nombres}/{primer_apellido}/{segundo_apellido}",
                        normalizarRequerido(nombres),
                        normalizarRequerido(primerApellido),
                        normalizarSegmento(segundoApellido))
                .retrieve()
                .body(String.class);
    }

    public String consultarPolizasPorFechaBeneficiario(String fechaNacimiento) {
        return restClient.get()
                .uri("/polizas/b/{fecha_nacimiento}", normalizarRequerido(fechaNacimiento))
                .retrieve()
                .body(String.class);
    }

    // ==================== GET / POST / PUT - CLIENTES ====================

    public String consultarCliente(String curp) {
        return restClient.get()
                .uri("/cliente/{curp}", normalizarRequerido(curp).toUpperCase())
                .retrieve()
                .body(String.class);
    }

    public String registrarCliente(String curp, String direccion, String fechaNacimiento,
                                   String nombres, String primerApellido, String segundoApellido) {
        return restClient.post()
                .uri("/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombres}/{primer_apellido}/{segundo_apellido}",
                        normalizarRequerido(curp).toUpperCase(),
                        normalizarRequerido(direccion),
                        normalizarRequerido(fechaNacimiento),
                        normalizarRequerido(nombres),
                        normalizarRequerido(primerApellido),
                        normalizarSegmento(segundoApellido))
                .retrieve()
                .body(String.class);
    }

    public String modificarCliente(String curp, String direccion, String fechaNacimiento,
                                   String nombres, String primerApellido, String segundoApellido) {
        return restClient.put()
                .uri("/cliente/{curp}/{direccion}/{fecha_nacimiento}/{nombres}/{primer_apellido}/{segundo_apellido}",
                        normalizarRequerido(curp).toUpperCase(),
                        normalizarRequerido(direccion),
                        normalizarRequerido(fechaNacimiento),
                        normalizarRequerido(nombres),
                        normalizarRequerido(primerApellido),
                        normalizarSegmento(segundoApellido))
                .retrieve()
                .body(String.class);
    }

    // ==================== POST / PUT / DELETE - PÓLIZAS ====================

    public String registrarPoliza(String clave, int tipo, double monto,
                                  String descripcion, String curpCliente) {
        String claveLimpia = normalizarRequerido(clave);
        String descripcionLimpia = normalizarRequerido(descripcion);
        String curpLimpia = normalizarRequerido(curpCliente).toUpperCase();

        String uriConstruida = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/poliza/{clave}/{tipo}/{monto}/{descripcion}/{curp_cliente}")
                .buildAndExpand(claveLimpia, tipo, monto, descripcionLimpia, curpLimpia)
                .encode()
                .toUriString();

       // System.out.println("[DEBUG URI POST]: " + uriConstruida);

        ResponseEntity<String> response = restClient.post()
                .uri(uriConstruida)
                .retrieve()
                .toEntity(String.class);

       // System.out.println("[DEBUG HTTP STATUS POST]: " + response.getStatusCode());
       // System.out.println("[DEBUG BODY POST]: " + response.getBody());

        return response.getBody();
    }

    public String modificarPoliza(String clave, int tipo, double monto,
                                  String descripcion, String curpCliente) {
        return restClient.put()
                .uri("/poliza/{clave}/{tipo}/{monto}/{descripcion}/{curp_cliente}",
                        normalizarRequerido(clave),
                        tipo,
                        monto,
                        normalizarRequerido(descripcion),
                        normalizarRequerido(curpCliente).toUpperCase())
                .retrieve()
                .body(String.class);
    }

    public String darDeBajaPoliza(String clave) {
        return restClient.delete()
                .uri("/poliza/{clave}", normalizarRequerido(clave))
                .retrieve()
                .body(String.class);
    }

    // ==================== GET / POST - BENEFICIARIOS ====================

    public String consultarBeneficiario(String fechaNacimiento, String clavePoliza,
                                         String nombres, String primerApellido, String segundoApellido) {
        return restClient.get()
                .uri("/beneficiario/{fecha_nacimiento}/{clave_poliza}/{nombres}/{primer_apellido}/{segundo_apellido}",
                        normalizarRequerido(fechaNacimiento),
                        normalizarRequerido(clavePoliza),
                        normalizarRequerido(nombres),
                        normalizarRequerido(primerApellido),
                        normalizarSegmento(segundoApellido))
                .retrieve()
                .body(String.class);
    }

    public String registrarBeneficiario(String fechaNacimiento, String clavePoliza, String porcentaje,
            String nombres, String primerApellido, String segundoApellido) {
String fechaLimpia = normalizarRequerido(fechaNacimiento);
String claveLimpia = normalizarRequerido(clavePoliza);
String porcentajeLimpio = normalizarRequerido(porcentaje);
String nombresLimpios = normalizarRequerido(nombres);
String primerApellidoLimpio = normalizarRequerido(primerApellido);
String segundoApellidoLimpio = normalizarSegmento(segundoApellido);

String uriConstruida = UriComponentsBuilder
.fromUriString(baseUrl)
.path("/beneficiario/{fecha_nacimiento}/{clave_poliza}/{porcentaje}/{nombres}/{primer_apellido}/{segundo_apellido}")
.buildAndExpand(fechaLimpia, claveLimpia, porcentajeLimpio, nombresLimpios, primerApellidoLimpio, segundoApellidoLimpio)
.encode()
.toUriString();

//System.out.println("[DEBUG URI POST BENEFICIARIO]: " + uriConstruida);

ResponseEntity<String> response = restClient.post()
.uri(uriConstruida)
.retrieve()
.toEntity(String.class);

//System.out.println("[DEBUG HTTP STATUS POST BENEFICIARIO]: " + response.getStatusCode());
//System.out.println("[DEBUG BODY POST BENEFICIARIO]: " + response.getBody());

return response.getBody();
}
}
