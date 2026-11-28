# IDSISTEMA — Sistema Intermediario de Gestión de Pólizas de Seguro

Prototipo distribuido desarrollado en Java con Spring Boot que actúa como intermediario entre clientes asegurados y el **Sistema Dueño de las Pólizas**, ofreciendo tanto una API REST como una interfaz interactiva de línea de comandos (CLI) y persistencia local en PostgreSQL.

* **Identificador del proyecto:** `mx.edu.uacm.is.slt.as.IDSISTEMA`
* **Institución:** Universidad Autónoma de la Ciudad de México (UACM) — Plantel San Lorenzo Tezonco
* **Asignatura:** Arquitectura de Software (Semestre 2026-II)
* **Equipo:** Bite Coding
  * Acatitla Zendejas Diego (`20-003-0242`)
  * Calderón Méndez Enoc Samuel (`22-003-0356`)
  * Gordillo Padilla Yadira Magali (`17-003-1078`)

---

## 1. Arquitectura del Sistema

La aplicación sigue los principios de **Arquitectura Limpia**, separando responsabilidades en capas bien definidas:

```text
VistasController (CLI) / REST Controllers (/api/*)
        │
        ▼
Services (ClienteService, PolizaService, BeneficiarioService)
        │
        ├──► Repositories (Spring Data JPA / Hibernate -> PostgreSQL)
        └──► SistemaDuenoClient (RestClient -> API REST Sistema Dueño)
```

### Estructura de Paquetes

```text
src/main/java/
├── mx/edu/uacm/is/slt/as/IDSISTEMA/
│   ├── IdsistemaApplication.java          # Clase principal (@SpringBootApplication, @ComponentScan)
│   ├── Cliente.java                       # Entidad JPA (tabla: clientes)
│   ├── ClienteRepository.java             # Repositorio JPA para Cliente
│   ├── Poliza.java                        # Entidad JPA (tabla: poliza)
│   ├── PolizaRepository.java              # Repositorio JPA para Poliza
│   ├── Beneficiario.java                  # Entidad JPA (tabla: beneficiarios)
│   ├── BeneficiarioRepository.java        # Repositorio JPA para Beneficiario
│   ├── controllers/
│   │   ├── ClienteController.java         # Endpoints REST /api/clientes
│   │   ├── PolizaController.java          # Endpoints REST /api/polizas
│   │   ├── BeneficiarioController.java    # Endpoints REST /api/beneficiarios
│   │   └── VistasController.java          # Interfaz CLI interactiva (CommandLineRunner)
│   └── services/
│       ├── ClienteService.java            # Reglas de negocio y sincronización de Clientes
│       ├── PolizaService.java             # Reglas de negocio y sincronización de Pólizas
│       └── BeneficiarioService.java       # Reglas de negocio, porcentajes y Beneficiarios
└── mx/uacm/polizas/client/
    └── SistemaDuenoClient.java            # Cliente HTTP RestClient hacia el Sistema Dueño
```

---

## 2. Requisitos Previos

Antes de descargar y ejecutar el proyecto, asegúrate de contar con las siguientes herramientas instaladas en tu equipo:

* **Java JDK:** Versión 17 (`JavaSE-17`) o superior.
* **Apache Maven:** Versión 3.9+ (o el wrapper `./mvnw` / `mvnw.cmd` incluido en el proyecto).
* **PostgreSQL:** Servidor activo en `localhost:5432`.
* **Git:** Para clonar el repositorio 
* **Conectividad de red:** Acceso a Internet para comunicarse con el servidor remoto del Sistema Dueño (`http://nachintoch.mx:8080`).

---

## 3. Modo de Descarga

Puedes obtener el código fuente del proyecto mediante la siguiente opcion:

Descarga directa en archivo comprimido (ZIP)
1. Ingresa al repositorio del proyecto en la plataforma correspondiente ([GitHub(https://github.com/diego123acat-dev/polizasArq)).
2. Haz clic en el botón **Code** (o **Descargar**) en la rama main y selecciona **Download ZIP**.
3. Extrae el archivo `.zip` en una carpeta local de tu preferencia (por ejemplo, `C:\Proyectos\IDSISTEMA` o `~/Proyectos/IDSISTEMA`).

---

## 4. Configuración de la Base de Datos y Entorno

### Paso 1: Crear la base de datos en PostgreSQL
Abre `pgAdmin` o la consola `psql` e inicia sesión con tu usuario de PostgreSQL (`postgres`), luego ejecuta:

```sql
CREATE DATABASE idsistema_db;
```
*(Nota: No es necesario crear las tablas manualmente; Hibernate las generará automáticamente al iniciar la aplicación).*

### Paso 2: Verificar `src/main/resources/application.properties`
Asegúrate de que las credenciales de PostgreSQL y la URL del Sistema Dueño coincidan con tu entorno local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/idsistema_db
spring.datasource.username=postgres
spring.datasource.password=uacm1
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
sistema.dueno.url=http://nachintoch.mx:8080
server.port=8082
```

---

## 5. Cómo Ejecutar el Proyecto

### Método: Ejecución desde un IDE (Eclipse / Spring Tool Suite / IntelliJ IDEA)
1. Abre tu IDE y selecciona **File > Import > Maven > Existing Maven Projects** (o **Open** sobre la carpeta raíz).
2. Selecciona la carpeta `IDSISTEMA` donde está el archivo `pom.xml` y espera a que Maven descargue todas las dependencias.
3. Verifica que el proyecto tenga configurado el **JRE System Library [JavaSE-17]** o superior.
4. Localiza la clase principal `mx.edu.uacm.is.slt.as.IDSISTEMA.IdsistemaApplication.java`.
5. Haz clic derecho sobre ella y selecciona **Run As > Java Application** (o **Spring Boot App**).

### Verificación de Inicio Exitoso
Una vez arrancado el sistema:
1. El servidor web embebido Tomcat quedará escuchando en el puerto **`8082`** (`http://localhost:8082`).
2. En la misma consola se desplegará automáticamente el menú interactivo de la interfaz CLI (`VistasController`).

---

## 6. Uso de la Interfaz de Consola (CLI)

El menú principal permite navegar por los tres módulos operativos:

```text
==============================================================
SISTEMA INTERMEDIARIO DE PÓLIZAS - MENÚ PRINCIPAL
==============================================================
[1] Gestión de Clientes
[2] Gestión de Pólizas
[3] Gestión de Beneficiarios
[0] Salir / Apagar el Sistema
==============================================================
```

* **Cancelación en cualquier captura:** Escribir `CANCELAR` en cualquier campo aborta la operación y regresa al menú anterior.
* **Formato de fechas:** `yyyy-MM-dd` (ejemplo: `1995-08-20`).
* **Tipos de póliza:** Valores enteros (`0`, `1`, `2`).
* **Validación de porcentajes de beneficiarios:** Entre `1` y `100`, verificando que la suma acumulada por póliza no exceda el $100\%$.

---

## 7. Endpoints de la API REST Local (`http://localhost:8082`)

### Clientes (`/api/clientes`)

| Método | Ruta | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/clientes/` | — | Verifica que el módulo de clientes esté activo. |
| `GET` | `/api/clientes/consultar/{curp}` | `curp` (Path) | Consulta un cliente por CURP (local o remoto). |
| `GET` | `/api/clientes/consultar` | — | Lista todos los clientes registrados localmente. |
| `POST` | `/api/clientes/registrar` | `curp`, `nombres`, `primerApellido`, `segundoApellido` (opc.), `direccion` (opc.), `fechaNacimiento` (opc., `yyyy-MM-dd`) | Registra un cliente en el Sistema Dueño y en PostgreSQL. |
| `PUT` | `/api/clientes/modificar/{curp}` | `curp` (Path), `direccion` (Query) | Actualiza la dirección del cliente en ambos sistemas. |

### Pólizas (`/api/polizas`)

| Método | Ruta | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/polizas/consultar/{clave}` | `clave` (Path) | Consulta una póliza por su clave en el Sistema Dueño. |
| `GET` | `/api/polizas/cliente/{curpCliente}` | `curpCliente` (Path) | Consulta todas las pólizas asociadas a un cliente. |
| `POST` | `/api/polizas/registrar` | `clave`, `tipo`, `monto`, `descripcion`, `curpCliente` | Da de alta una póliza validando cliente previo. |
| `PUT` | `/api/polizas/modificar/{clave}` | `clave` (Path), `monto`, `descripcion` | Actualiza monto y descripción de una póliza. |
| `DELETE` | `/api/polizas/baja/{clave}` | `clave` (Path) | Da de baja una póliza en el Sistema Dueño y localmente. |

### Beneficiarios (`/api/beneficiarios`)

| Método | Ruta | Parámetros | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/beneficiarios/consultar` | — | Lista todos los beneficiarios registrados localmente. |
| `POST` | `/api/beneficiarios/registrar` | `clavePoliza`, `curp`, `nombres`, `primerApellido`, `segundoApellido` (opc.), `direccion` (opc.), `fechaNacimiento` (opc.) | Registra un beneficiario en el Sistema Dueño y en PostgreSQL. |
| `DELETE` | `/api/beneficiarios/eliminar/{curp}` | `curp` (Path) | Elimina un beneficiario de la base de datos local por CURP. |

---

## 8. Manejo de Errores y Códigos de Notificación

El sistema estandariza todas las respuestas hacia el usuario mediante prefijos claros:

* `[ÉXITO]` / `[RESPUESTA RECIBIDA]`: Operación completada y carga JSON formateada en lista legible.
* `[ERROR DE VALIDACIÓN]`: Datos obligatorios faltantes, formatos inválidos o suma de porcentajes $> 100\%$.
* `[ALERTA]`: Registro inexistente en el Sistema Dueño o en la base local.
* `[EX-CYR]` / `[EX-PYR]` / `[EX-BYR]`: Registro duplicado (Cliente, Póliza o Beneficiario ya registrado).
* `[EX-CNR]`: Cliente no registrado al intentar asociar una póliza.
* `[EX-DND]` / `[ERROR CRÍTICO]`: Fallo de comunicación o indisponibilidad del Sistema Dueño.
- `[EX-DND]` / `[ERROR CRÍTICO]`: Fallo de comunicación o indisponibilidad del Sistema Dueño.
