# Guía de Presentación - MedicalConsulta

Este documento sirve como pauta para la **Evaluación 3: Presentación de Código**. Contiene la información técnica y arquitectónica del proyecto para apoyar la defensa oral.

---

## 1. Preparación del Entorno (Puntos 1, 2 y 3)

*   **Estado Inicial:** El proyecto debe estar abierto en Android Studio y el emulador (o dispositivo físico) corriendo antes de conectar el proyector.
*   **Ejecución:** Presionar el botón "Run" (Shift+F10).
*   **Manejo de Errores (Troubleshooting):**
    *   *Si la app crashea:* Abrir la pestaña **Logcat** en la parte inferior, filtrar por "Error" o "Fatal". Buscar excepciones comunes (ej. `SQLiteConstraintException` si falla la BD, o `NullPointerException`).
    *   *Si no compila:* Ir a **Build > Clean Project** y luego **Build > Rebuild Project**. Verificar `gradle.properties` (JVM version).

---

## 2. Arquitectura: MVVM (Punto 6)

El proyecto implementa **Model-View-ViewModel (MVVM)** para separar la lógica de presentación de la lógica de negocio y datos.

*   **VIEW (Vista):**
    *   **Tecnología:** Jetpack Compose.
    *   **Responsabilidad:** Renderizar la UI y capturar eventos del usuario. No contiene lógica de negocio.
    *   **Ubicación:** `cl.duoc.medicalconsulta.ui.screen` (ej. `AgendarCitaScreen`, `HistorialCitasScreen`).
*   **VIEWMODEL:**
    *   **Tecnología:** `androidx.lifecycle.ViewModel`, `StateFlow`.
    *   **Responsabilidad:** Mantiene el estado de la UI (`CitaUIState`), maneja la lógica de validación y comunica la Vista con el Modelo. Sobrevive a rotaciones de pantalla.
    *   **Ubicación:** `cl.duoc.medicalconsulta.viewmodel` (ej. `CitaViewModel`).
*   **MODEL (Modelo):**
    *   **Tecnología:** Room (SQLite), Repositorios.
    *   **Responsabilidad:** Abstraer el origen de datos.
    *   **Ubicación:** `cl.duoc.medicalconsulta.model` (Entities, DAO, Repository).

---

## 3. Interfaz de Usuario y Navegación (Punto 4)

### a) Componentes de Formulario (`AgendarCitaScreen`)
*   **InputText:** Componente reutilizable (`OutlinedTextField`) para ingresar Nombre, RUT, Fecha, etc.
*   **Estado:** El texto ingresado se delega al ViewModel mediante eventos (ej. `viewModel.onNombreChange`). La UI se redibuja automáticamente cuando el `StateFlow` cambia.
*   **Feedback:** Los errores de validación se muestran en rojo debajo de cada campo usando `Text` condicionales basados en `estado.errores`.

### b) Navegación (`AppNav.kt`)
*   **Herramienta:** Navigation Compose.
*   **Estructura:**
    *   `NavHost`: Contenedor principal que gestiona el intercambio de pantallas.
    *   `DrawerScaffold`: Layout personalizado que incluye el menú lateral (`ModalNavigationDrawer`) y la barra superior (`TopAppBar`).
*   **Rutas:** Definidas en el objeto `Routes` (`Start`, `AgendarCita`, `Historial`).
*   **Paso de Argumentos:** Para editar, se pasa el `citaId` en la ruta: `"agendar_cita?citaId={id}"`.

### c) Comunicación Vista-ViewModel
1.  La Vista instancia al ViewModel: `val viewModel: CitaViewModel = viewModel(...)`.
2.  La Vista **observa** el estado: `val estado by viewModel.estado.collectAsState()`.
3.  La Vista **envía** eventos: `onClick = { viewModel.guardar() }`.

---

## 4. Persistencia de Datos: Room (Punto 5)

### a) Configuración
*   **Librerías:** `androidx.room:room-runtime`, `room-ktx` y `room-compiler` (KSP) en `build.gradle.kts`.
*   **Database:** Clase `AppDatabase` anotada con `@Database`. Crea la instancia de SQLite.

### b) Componentes (DAO, Repository, Entity)
1.  **Entity (`CitaEntity`):** Representa la tabla `citas` en SQLite. Anotada con `@Entity`.
2.  **DAO (`CitaDao`):** Interfaz con métodos SQL (`@Insert`, `@Query`, `@Delete`).
3.  **Repository (`CitaRepository`):** Clase intermediaria. El ViewModel llama al repositorio, y el repositorio llama al DAO. Esto permite cambiar la fuente de datos (ej. a una API) sin afectar al ViewModel.

### c) Pruebas en Vivo (Demo)
*   **Inserción:** Llenar formulario en "Agendar Cita" -> Guardar -> Verificar Toast de éxito.
*   **Obtención:** Ir a "Historial" -> Ver la lista actualizada (`LazyColumn`).
*   **Actualización:** Click en "Editar" en el Historial -> Modificar datos -> Guardar.
*   **Eliminación:** Click en "Eliminar" en el Historial -> El ítem desaparece.

---

## 5. Validaciones (Punto 7)

Ubicadas en `CitaViewModel.kt` -> función `onAgendarCitaOActualizarCita`.
*   **RUT:** Regex para formato `12345678-9`.
*   **Campos vacíos:** Verificación con `isBlank()`.
*   **Fecha/Hora:** Regex para formato `dd/mm/yyyy` y `HH:MM`.
*   **Mecanismo:** Al pulsar "Agendar", se actualiza el objeto `CitaErrores` dentro del estado. Si `tieneErrores()` es true, se detiene el guardado y la UI muestra los mensajes.

---

## 6. Respuestas a Preguntas Frecuentes (Punto 8)

**P: ¿Dónde agregaría el registro de Pacientes (nueva tabla)?**
R:
1.  Crear `PacienteEntity` (@Entity).
2.  Crear `PacienteDao` (interface).
3.  Agregar `PacienteEntity` a `AppDatabase` y declarar el método abstracto del DAO.
4.  Crear `PacienteRepository`.
5.  Crear `PacienteViewModel` y las pantallas de UI correspondientes.

**P: ¿Cómo mejoraría la UI?**
R: Implementando DatePickers nativos de Material3 en lugar de ingresar texto manual para fechas, y agregando animaciones de transición en el `NavHost`.

**P: ¿Por qué usa Corrutinas?**
R: (`viewModelScope.launch`) Para ejecutar operaciones de base de datos (I/O) fuera del hilo principal (Main Thread) y evitar que la aplicación se congele (ANR).
