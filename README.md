# MedicalConsulta - Proyecto de Aplicación Móvil

## 1. Alcance del Proyecto
MedicalConsulta es una aplicación móvil desarrollada en Android nativo (Kotlin) diseñada para la gestión de consultas médicas. La aplicación permite a los usuarios visualizar profesionales disponibles, agendar horas y gestionar sus datos personales. El sistema está diseñado para ser una herramienta eficiente tanto para pacientes como para la administración de recursos médicos, con un enfoque en una experiencia de usuario fluida y persistencia de datos local segura.

---

## 2. Arquitectura de Software

El proyecto implementa la arquitectura **MVVM (Model-View-ViewModel)**, recomendada por Google para el desarrollo moderno de Android. Esta arquitectura separa claramente las responsabilidades de la aplicación en tres capas principales, facilitando el mantenimiento, la escalabilidad y las pruebas.

### Cómo se aplica MVVM en MedicalConsulta:

*   **Model (Modelo):**
    *   Encargado de la gestión de datos y la lógica de negocio.
    *   Utiliza **Room** como base de datos SQLite local.
    *   Estructura:
        *   **Entities:** Clases de datos que representan las tablas de la base de datos (ej. `ProfesionalEntity`).
        *   **DAO (Data Access Object):** Interfaces que definen las operaciones SQL (consultas, inserciones, actualizaciones).
        *   **Repository:** Clase intermediaria que abstrae la fuente de datos (el DAO) para el ViewModel, exponiendo funciones limpias y manejando la ejecución en hilos secundarios (Corrutinas).
    *   *Ubicación en código:* `cl.duoc.medicalconsulta.model`

*   **View (Vista):**
    *   Responsable de la interfaz de usuario (UI) y de mostrar los datos al usuario.
    *   Construida íntegramente con **Jetpack Compose**, el kit de herramientas moderno declarativo de Android.
    *   Observa el estado expuesto por el ViewModel y se recompone automáticamente cuando los datos cambian.
    *   Incluye Pantallas (Screens) y Componentes reutilizables.
    *   *Ubicación en código:* `cl.duoc.medicalconsulta.ui`

*   **ViewModel:**
    *   Actúa como puente entre el Modelo y la Vista.
    *   Almacena y gestiona el estado de la UI (UI State).
    *   Expone datos a través de `StateFlow` o `LiveData` que la vista observa.
    *   Llama a las funciones del Repositorio para realizar operaciones de base de datos sin bloquear el hilo principal de la UI.
    *   Sobrevive a cambios de configuración (como rotar la pantalla).
    *   *Ubicación en código:* `cl.duoc.medicalconsulta.viewmodel`

---

## 3. Guía para la Evaluación 3 (Presentación de Código)

### Preparación del Entorno
*   **IDE:** Android Studio Iguana/Jellyfish o superior.
*   **Ejecución:** Se recomienda tener el emulador corriendo o el dispositivo físico conectado antes de iniciar la presentación para evitar tiempos de carga.
*   **Solución de Problemas:**
    *   Si la app no inicia: Verificar `Logcat` filtrando por "Error".
    *   Revisar conexión a base de datos Room.
    *   Limpiar y reconstruir el proyecto: *Build > Clean Project* y luego *Build > Rebuild Project*.

### 4. Interfaz de Usuario (UI) y Navegación
*   **Navegación:** Implementada con **Navigation Compose**. Se define un `NavHost` en `AppNav.kt` que gestiona el grafo de navegación y las rutas entre pantallas (`Composable`).
*   **Formularios:** Utilizan componentes de Material3 (`OutlinedTextField`, `Button`, etc.). El estado del texto se maneja en variables observables (`remember` o `ViewModel state`) para reactividad inmediata.
*   **Comunicación Vista-ViewModel:** La vista instancia el ViewModel (usando `viewModel()`) y llama a sus métodos (ej. `viewModel.guardar()`) ante eventos de usuario (clicks). La vista observa los flujos de datos del ViewModel (`collectAsState`) para actualizarse.

### 5. Persistencia de Datos (Room)
La aplicación utiliza **Room Persistence Library** para manejar una base de datos SQLite local.

*   **Configuración:**
    *   **Librerías:** Definidas en `build.gradle.kts` (Room Runtime, KTX, Compiler/KSP).
    *   **Database:** Clase abstracta anotada con `@Database` que hereda de `RoomDatabase`. Define las entidades y la versión.
    *   **DAO:** Interface anotada con `@Dao`. Contiene métodos con anotaciones `@Query`, `@Insert`, `@Update`, `@Delete`.
    *   **Repository:** Inyecta el DAO y expone funciones `suspend` o `Flow` al ViewModel.

*   **Pruebas de Persistencia:**
    *   **Inserción:** Crear un nuevo registro (ej. nuevo paciente/consulta) desde el formulario y verificar que aparezca en la lista.
    *   **Obtención:** Visualizar la lista de datos cargados desde la BD al iniciar la pantalla.

### 6. Validaciones
Las validaciones se realizan antes de llamar al método de guardado en el ViewModel o directamente en la UI.
*   **Ejemplo:** Verificar que campos obligatorios no estén vacíos.
*   **Feedback:** Se muestra un mensaje de error (Toast o texto en rojo) si la validación falla.

### Preguntas Frecuentes (Cheat Sheet)
*   **¿Dónde agregaría una nueva funcionalidad?**
    1.  Crear la Entidad (si requiere persistencia).
    2.  Actualizar DAO y Repository.
    3.  Crear/Actualizar el ViewModel.
    4.  Crear la Pantalla (Screen) en Compose y agregarla a la Navegación.

*   **¿Cómo maneja la concurrencia?**
    *   Usamos **Corrutinas de Kotlin** (`viewModelScope.launch`) para realizar operaciones de base de datos en un hilo secundario (IO Dispatcher) y no congelar la interfaz de usuario.
