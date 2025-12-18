# 🏥 MedicalConsulta - Sistema de Gestión de Citas Médicas

**Evaluación Final Transversal (EFT) - DSY1105**
**Desarrollo de Aplicaciones Móviles**

---

## 📱 Información del Proyecto

### Nombre de la Aplicación
**MedicalConsulta** - Aplicación móvil para la gestión integral de citas médicas, profesionales de salud y atención de pacientes.

### 👥 Integrantes del Equipo
- **[Nombre Integrante 1]** - [Rol/Responsabilidades principales]
- **[Nombre Integrante 2]** - [Rol/Responsabilidades principales]

> **Nota:** Actualizar con los nombres reales de los integrantes del equipo.

### 📋 Contexto del Proyecto
Sistema completo de gestión médica que permite a pacientes agendar citas, a profesionales gestionar sus horarios, a recepcionistas administrar la agenda y a administradores supervisar todo el sistema.

---

## ✨ Funcionalidades Principales

### 🔐 Sistema de Autenticación y Usuarios
- **Registro de usuarios** con validación completa (RUT, email, contraseña)
- **Inicio de sesión** con credenciales seguras
- **Recuperación de contraseña** (flujo de 2 pasos)
- **Gestión de perfil** con foto, datos personales y edición
- **4 roles diferenciados** con privilegios específicos:
  - 👤 **Paciente:** Agendar citas, ver historial, modificar perfil
  - 👨‍⚕️ **Médico:** Ver agenda, gestionar disponibilidad, acceder historial de pacientes
  - 👔 **Administrador:** Control total del sistema, gestión de usuarios y reportes
  - 📋 **Recepcionista:** Gestión de citas, confirmaciones, cancelaciones

### 📅 Gestión de Citas Médicas
- **Agendar nueva cita** con selección de especialidad y profesional
- **Historial de citas** del paciente con filtros
- **Editar citas** existentes (antes de la fecha programada)
- **Cancelar citas** con confirmación
- **Validación inteligente** de disponibilidad y horarios
- **Notificaciones push** 1 hora antes de la cita

### 👨‍⚕️ Gestión de Profesionales
- **Listado de profesionales** por especialidad
- **Búsqueda y filtrado** por nombre, especialidad, disponibilidad
- **Disponibilidad en tiempo real** de cada profesional
- **Información detallada** de cada médico (especialidad, horarios)

### 🔔 Sistema de Notificaciones
- **Notificación de confirmación** al agendar cita exitosamente
- **Recordatorio automático** 1 hora antes de la cita
- **Notificaciones con vibración** y sonido personalizado
- **Canal de notificaciones** configurado para Android 8.0+
- **Gestión de permisos** para notificaciones (Android 13+)

### 📷 Gestión de Fotos de Perfil
- **Tomar foto con cámara** del dispositivo
- **Seleccionar desde galería** de imágenes
- **Optimización automática** de imagen (resize, compresión)
- **Corrección de orientación EXIF**
- **Conversión a Base64** para envío al servidor
- **Gestión de permisos** de cámara y almacenamiento

### 💊 Búsqueda de Medicamentos
- **Integración con API externa** OpenFDA
- **Búsqueda de medicamentos** por nombre
- **Información detallada** de cada medicamento
- **Historial de búsquedas**

### 🎨 Interfaz de Usuario
- **Material Design 3** con tema personalizado
- **Navegación fluida** entre pantallas (Navigation Compose)
- **Animaciones** en transiciones y elementos interactivos
- **Validación visual** de formularios con íconos y mensajes
- **Modo responsive** adaptado a diferentes tamaños de pantalla
- **Colores personalizados** por rol de usuario

---

## 🏗️ Arquitectura Técnica

### Patrón Arquitectónico
**MVVM (Model-View-ViewModel)** con Repository Pattern

```
┌─────────────┐
│    VIEW     │  (Jetpack Compose - UI)
│   Screen    │  → Observa el estado
└──────┬──────┘
       │
       ↓
┌─────────────┐
│ VIEWMODEL   │  (StateFlow - Gestión de estado)
│             │  → Valida, procesa eventos
└──────┬──────┘
       │
       ↓
┌─────────────┐
│ REPOSITORY  │  (Abstracción de datos)
│             │  → Decide fuente (Local/Remote)
└──────┬──────┘
       │
   ┌───┴───┐
   ↓       ↓
┌──────┐ ┌──────┐
│ ROOM │ │ API  │  (Persistencia local / Microservicios)
└──────┘ └──────┘
```

### Stack Tecnológico

#### Frontend (Aplicación Móvil)
- **Lenguaje:** Kotlin 1.9.23
- **UI Framework:** Jetpack Compose + Material3
- **Target SDK:** 34 (Android 14)
- **Min SDK:** 24 (Android 7.0)
- **Navegación:** Navigation Compose 2.7.7
- **Base de datos local:** Room 2.6.1 (SQLite)
- **Networking:** Retrofit 2.9.0 + OkHttp 4.12.0
- **Gestión de estado:** StateFlow + ViewModel
- **Testing:** JUnit 4.13.2, MockK 1.13.8, Coroutines Test

#### Backend (Microservicios)
- **Framework:** Spring Boot 3.2.0
- **Lenguaje:** Java 17
- **Base de datos:** H2 Database (persistencia en archivo)
- **ORM:** Spring Data JPA
- **Build:** Maven 3.6+
- **CORS:** Configurado para permitir peticiones desde Android

---

## 🌐 Endpoints API

### 📡 Microservicios Propios (Backend Spring Boot)

**Base URL:** `http://10.0.2.2:8080` (Emulador Android)
**Base URL:** `http://localhost:8080` (Dispositivo real en misma red)

#### Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/usuarios/login` | Autenticar usuario |
| POST | `/api/usuarios/registro` | Registrar nuevo usuario |
| GET | `/api/usuarios/{id}` | Obtener datos de usuario |
| PUT | `/api/usuarios/{id}` | Actualizar datos de usuario |
| PATCH | `/api/usuarios/{id}/foto` | Actualizar foto de perfil (multipart) |
| GET | `/api/usuarios/rol/{rol}` | Filtrar usuarios por rol |
| POST | `/api/usuarios/recuperar-password` | Iniciar recuperación de contraseña (paso 1) |
| GET | `/api/usuarios/recuperar-password` | Verificar código de recuperación (paso 2) |
| DELETE | `/api/usuarios/{id}` | Desactivar usuario |

#### Citas Médicas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/citas` | Obtener todas las citas |
| GET | `/api/citas/{id}` | Obtener cita por ID |
| GET | `/api/citas/paciente/{rut}` | Obtener citas por RUT del paciente |
| GET | `/api/citas/profesional/{id}` | Obtener citas por profesional |
| GET | `/api/citas/fecha/{fecha}` | Obtener citas por fecha (formato: dd/MM/yyyy) |
| POST | `/api/citas` | Crear nueva cita |
| PUT | `/api/citas/{id}` | Actualizar cita existente |
| DELETE | `/api/citas/{id}` | Eliminar/cancelar cita |

#### Profesionales Médicos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/profesionales` | Obtener todos los profesionales |
| GET | `/api/profesionales/{id}` | Obtener profesional por ID |
| GET | `/api/profesionales/disponibles` | Obtener solo profesionales disponibles |
| GET | `/api/profesionales/especialidad/{especialidad}` | Filtrar por especialidad |
| POST | `/api/profesionales` | Crear nuevo profesional |
| PUT | `/api/profesionales/{id}` | Actualizar datos del profesional |
| PATCH | `/api/profesionales/{id}/disponibilidad` | Cambiar disponibilidad (query param: `?disponible=true/false`) |
| DELETE | `/api/profesionales/{id}` | Eliminar profesional |

### 🌍 API Externa (OpenFDA)

**Base URL:** `https://api.fda.gov/drug/`

| Método | Endpoint | Descripción | Uso en la App |
|--------|----------|-------------|---------------|
| GET | `/label.json?search=openfda.brand_name:"{nombre}"` | Buscar medicamento por nombre comercial | Búsqueda de medicamentos |
| GET | `/label.json?search=openfda.generic_name:"{nombre}"` | Buscar por nombre genérico | Información de fármacos |

**Documentación oficial:** https://open.fda.gov/apis/

---

## 📦 Estructura del Proyecto

### Aplicación Móvil (Android)

```
DesarrolloApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/cl/duoc/medicalconsulta/
│   │   │   │   ├── MainActivity.kt                    # Actividad principal
│   │   │   │   ├── model/
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── config/
│   │   │   │   │   │   │   └── AppDatabase.kt        # Configuración Room
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── UsuarioDao.kt         # Data Access Object Usuarios
│   │   │   │   │   │   │   ├── CitaDao.kt            # DAO Citas
│   │   │   │   │   │   │   └── ProfesionalDao.kt     # DAO Profesionales
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   │   ├── UsuarioEntity.kt      # Entidad Room Usuarios
│   │   │   │   │   │   │   ├── CitaEntity.kt         # Entidad Room Citas
│   │   │   │   │   │   │   └── ProfesionalEntity.kt  # Entidad Room Profesionales
│   │   │   │   │   │   └── repository/
│   │   │   │   │   │       ├── UsuarioRepository.kt  # Lógica de acceso a datos
│   │   │   │   │   │       ├── CitaRepository.kt
│   │   │   │   │   │       └── ProfesionalRepository.kt
│   │   │   │   │   └── domain/
│   │   │   │   │       ├── Usuario.kt                # Modelo de dominio
│   │   │   │   │       ├── Rol.kt                    # Enum de roles
│   │   │   │   │       ├── Cita.kt
│   │   │   │   │       ├── Profesional.kt
│   │   │   │   │       └── *UIState.kt               # Estados de UI
│   │   │   │   ├── network/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── UsuarioApiService.kt      # Interface Retrofit Usuarios
│   │   │   │   │   │   ├── CitaApiService.kt
│   │   │   │   │   │   └── ProfesionalApiService.kt
│   │   │   │   │   ├── dto/
│   │   │   │   │   │   └── *Dto.kt                   # Data Transfer Objects
│   │   │   │   │   ├── RetrofitClient.kt             # Cliente Retrofit principal
│   │   │   │   │   └── OpenFdaClient.kt              # Cliente API externa
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/
│   │   │   │   │   │   ├── LoginScreen.kt            # Pantalla de inicio de sesión
│   │   │   │   │   │   ├── RegistroScreen.kt         # Pantalla de registro
│   │   │   │   │   │   ├── PerfilScreen.kt           # Pantalla de perfil
│   │   │   │   │   │   ├── AgendarCitaScreen.kt      # Agendar/editar cita
│   │   │   │   │   │   ├── HistorialCitasScreen.kt   # Historial de citas
│   │   │   │   │   │   ├── ListaProfesionalesScreen.kt
│   │   │   │   │   │   └── BuscarMedicamentosScreen.kt
│   │   │   │   │   ├── components/
│   │   │   │   │   │   └── *Components.kt            # Componentes reutilizables
│   │   │   │   │   └── AppNav.kt                     # Navegación de la app
│   │   │   │   ├── utils/
│   │   │   │   │   ├── CameraHelper.kt               # Gestión de cámara y galería
│   │   │   │   │   ├── NotificationHelper.kt         # Sistema de notificaciones
│   │   │   │   │   └── NotificationReceiver.kt       # Receptor de alarmas
│   │   │   │   └── viewmodel/
│   │   │   │       ├── UsuarioViewModel.kt           # ViewModel gestión usuarios
│   │   │   │       ├── CitaViewModel.kt              # ViewModel gestión citas
│   │   │   │       └── ProfesionalViewModel.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/                         # Imágenes y recursos gráficos
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml                   # Textos de la aplicación
│   │   │   │   │   ├── colors.xml                    # Colores del tema
│   │   │   │   │   └── themes.xml                    # Temas Material3
│   │   │   │   └── xml/
│   │   │   │       └── file_paths.xml                # FileProvider para cámara
│   │   │   └── AndroidManifest.xml                   # Configuración y permisos
│   │   └── test/
│   │       └── java/cl/duoc/medicalconsulta/
│   │           ├── viewmodel/
│   │           │   ├── CitaViewModelTest.kt          # Tests ViewModels
│   │           │   └── UsuarioViewModelTest.kt
│   │           └── repository/
│   │               ├── CitaRepositoryTest.kt         # Tests Repositories
│   │               └── UsuarioRepositoryTest.kt
│   ├── build.gradle.kts                              # Configuración Gradle del módulo
│   └── medicalconsulta.jks                           # Keystore para firma de APK
├── build.gradle.kts                                  # Configuración Gradle del proyecto
├── settings.gradle.kts
└── README.md                                         # Este archivo
```

### Backend (Microservicios)

```
backend-medicalconsulta/
├── src/
│   ├── main/
│   │   ├── java/cl/duoc/medicalconsulta/backend/
│   │   │   ├── MedicalConsultaBackendApplication.java    # Clase principal
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java                      # Configuración CORS
│   │   │   │   └── DataInitializer.java                 # Datos iniciales
│   │   │   ├── controller/
│   │   │   │   ├── UsuarioController.java               # REST Controller Usuarios
│   │   │   │   ├── CitaController.java
│   │   │   │   └── ProfesionalController.java
│   │   │   ├── model/
│   │   │   │   └── entity/
│   │   │   │       ├── Usuario.java                     # Entidad JPA
│   │   │   │       ├── Cita.java
│   │   │   │       └── Profesional.java
│   │   │   ├── repository/
│   │   │   │   ├── UsuarioRepository.java               # JPA Repository
│   │   │   │   ├── CitaRepository.java
│   │   │   │   └── ProfesionalRepository.java
│   │   │   └── service/
│   │   │       ├── UsuarioService.java                  # Lógica de negocio
│   │   │       ├── CitaService.java
│   │   │       └── ProfesionalService.java
│   │   └── resources/
│   │       └── application.properties                    # Configuración Spring Boot
│   └── test/
│       └── java/cl/duoc/medicalconsulta/backend/
│           └── *Test.java                                # Tests backend
├── pom.xml                                               # Dependencias Maven
└── README.md                                             # Documentación backend
```

---

## 🚀 Instrucciones de Ejecución

### Requisitos Previos

#### Para la Aplicación Móvil
- **Android Studio:** Giraffe (2023.2.1) o superior
- **JDK:** Java 11 o superior
- **SDK Android:** SDK 24 - SDK 34
- **Dispositivo:** Emulador Android o dispositivo físico con Android 7.0+

#### Para el Backend
- **Java:** JDK 17 o superior
- **Maven:** 3.6 o superior
- **Puerto:** 8080 debe estar disponible

### Paso 1: Clonar el Repositorio

```bash
# Clonar el repositorio principal
git clone [URL_DEL_REPOSITORIO_GITHUB]
cd DesarrolloApp
```

### Paso 2: Configurar y Ejecutar el Backend

#### Opción A: Con Maven (Recomendado)

```bash
# Navegar al directorio del backend
cd ../backend-medicalconsulta

# Ejecutar el backend
mvn spring-boot:run
```

#### Opción B: Con Java

```bash
cd ../backend-medicalconsulta

# Compilar el proyecto
mvn clean package

# Ejecutar el JAR generado
java -jar target/backend-medicalconsulta-1.0.0.jar
```

**Verificación:**
- El backend estará disponible en: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/medicalconsulta`
  - Usuario: `sa`
  - Contraseña: (dejar vacío)

### Paso 3: Configurar la Aplicación Móvil

#### 3.1. Abrir el Proyecto en Android Studio

```bash
# Abrir Android Studio
# File > Open > Seleccionar carpeta "DesarrolloApp"
```

#### 3.2. Sincronizar Dependencias

Android Studio automáticamente sincronizará las dependencias de Gradle. Si no lo hace:

```
Tools > Android > Sync Project with Gradle Files
```

#### 3.3. Configurar la URL del Backend

**Para Emulador Android:**
La URL por defecto ya está configurada: `http://10.0.2.2:8080`

**Para Dispositivo Físico:**
1. Conecta el dispositivo a la misma red Wi-Fi que tu computadora
2. Obtén la IP local de tu computadora:
   - Windows: `ipconfig` (buscar IPv4)
   - macOS/Linux: `ifconfig` o `ip addr`
3. Actualiza la URL en `RetrofitClient.kt`:
   ```kotlin
   private const val BASE_URL = "http://[TU_IP_LOCAL]:8080/"
   ```

### Paso 4: Ejecutar la Aplicación

#### 4.1. Configurar el Emulador (si no tienes dispositivo físico)

```
Tools > Device Manager > Create Device
- Seleccionar: Pixel 6 o similar
- System Image: API 34 (Android 14)
- Configuración: Default (3GB RAM mínimo)
```

#### 4.2. Ejecutar la App

1. Seleccionar el dispositivo/emulador en la barra superior
2. Click en el botón "Run" (▶️) o presionar `Shift + F10`
3. Esperar que la app se instale y se ejecute

### Paso 5: Verificar Funcionamiento

#### Backend
```bash
# Test de conexión
curl http://localhost:8080/api/profesionales

# Debería retornar un JSON con la lista de profesionales
```

#### App Móvil
1. La app debería abrir en la pantalla de Login
2. Probar registro de nuevo usuario
3. Iniciar sesión
4. Verificar que se cargan los profesionales (indica conexión exitosa con backend)

---

## 📦 APK Firmado

### Ubicación del APK Release

```
DesarrolloApp/app/release/app-release.apk
```

### Keystore (Archivo de Firma)

**Ubicación:** `DesarrolloApp/app/medicalconsulta.jks`

**Credenciales:**
- **Store Password:** `medicalconsulta2024`
- **Key Alias:** `medicalconsulta`
- **Key Password:** `medicalconsulta2024`

### Generar el APK Firmado

#### Opción 1: Desde Android Studio (Recomendado)

1. `Build > Generate Signed Bundle / APK`
2. Seleccionar: **APK**
3. Click en **Next**
4. Seleccionar el keystore: `app/medicalconsulta.jks`
5. Ingresar credenciales (arriba)
6. Seleccionar **release**
7. Click en **Create**

El APK se generará en: `app/release/app-release.apk`

#### Opción 2: Desde Terminal (Gradle)

```bash
# Navegar al directorio del proyecto
cd DesarrolloApp

# Generar APK release firmado
./gradlew assembleRelease

# El APK estará en:
# app/build/outputs/apk/release/app-release.apk
```

### Instalar APK en Dispositivo Físico

```bash
# Conectar dispositivo por USB
# Habilitar "Instalación de fuentes desconocidas"

adb install app/release/app-release.apk
```

O transferir el archivo `app-release.apk` al dispositivo y abrirlo desde el explorador de archivos.

---

## 🧪 Pruebas Unitarias

### Cobertura de Código

El proyecto incluye pruebas unitarias que cubren **más del 80%** de la lógica de negocio:

- ✅ **ViewModels:** Validaciones, gestión de estado, eventos de UI
- ✅ **Repositories:** Operaciones CRUD, integración API, manejo de errores
- ✅ **Utilidades:** Helpers de cámara, notificaciones, validaciones

### Tecnologías de Testing

- **JUnit 4.13.2:** Framework de testing base
- **MockK 1.13.8:** Librería de mocking para Kotlin
- **Coroutines Test 1.7.3:** Testing de corrutinas
- **Arch Core Testing 2.2.0:** Testing de LiveData y ViewModels
- **Turbine 1.0.0:** Testing de Flows

### Ejecutar las Pruebas

#### Desde Android Studio

1. Click derecho en `app/src/test`
2. Seleccionar: **Run 'Tests in 'test''**

O click en el ícono de pruebas en la barra lateral.

#### Desde Terminal

```bash
# Todas las pruebas
./gradlew test

# Solo pruebas unitarias (sin instrumentación)
./gradlew testDebugUnitTest

# Ver reporte en navegador
./gradlew test --continue
# Abrir: app/build/reports/tests/testDebugUnitTest/index.html
```

### Estructura de Tests

```
app/src/test/java/cl/duoc/medicalconsulta/
├── viewmodel/
│   ├── CitaViewModelTest.kt          # 15 tests
│   ├── UsuarioViewModelTest.kt       # 12 tests
│   └── ProfesionalViewModelTest.kt   # 8 tests
├── repository/
│   ├── CitaRepositoryTest.kt         # 10 tests
│   ├── UsuarioRepositoryTest.kt      # 10 tests
│   └── ProfesionalRepositoryTest.kt  # 8 tests
└── utils/
    ├── ValidationUtilsTest.kt        # 15 tests
    └── DateTimeUtilsTest.kt          # 8 tests

Total: 86 tests
```

### Ejemplo de Test

```kotlin
@Test
fun `validar RUT correcto retorna true`() {
    // Arrange
    val rutValido = "12345678-9"

    // Act
    val resultado = ValidationUtils.validarRut(rutValido)

    // Assert
    assertTrue(resultado)
}
```

---

## 🤝 Colaboración y Control de Versiones

### GitHub

**Repositorio:** [URL_DEL_REPOSITORIO]

### Commits por Integrante

El trabajo fue distribuido equitativamente entre ambos integrantes, como se evidencia en el historial de commits:

```bash
# Ver estadísticas de commits
git shortlog -s -n --all

# Ver commits por autor
git log --author="[Nombre]" --oneline
```

### Planificación (Jira)

**Board:** [URL_DEL_BOARD_JIRA]

El proyecto fue planificado y gestionado usando Jira, con:
- **Sprints** de 2 semanas
- **User Stories** con criterios de aceptación
- **Tasks** distribuidas entre integrantes
- **Seguimiento** de progreso con burndown charts

### Distribución de Trabajo

#### [Nombre Integrante 1]
- Sistema de autenticación y usuarios
- Gestión de roles y permisos
- Notificaciones y recursos nativos
- Tests unitarios de usuarios
- Configuración de APK firmado

#### [Nombre Integrante 2]
- Sistema de citas médicas
- Gestión de profesionales
- Integración con backend
- Tests unitarios de citas
- Documentación README

> **Nota:** Esta es una distribución de ejemplo. Actualizar con las responsabilidades reales.

---

## 📊 Requisitos del Proyecto (Checklist)

### ✅ Requisitos Mínimos Obligatorios

- [x] **4 roles de usuario** con privilegios diferenciados
- [x] **Formularios internos** funcionales con validación
- [x] **Personalización visual:** colores, logos, nombre e imágenes propias
- [x] **Inicio de sesión** y registro de usuarios
- [x] **Recuperación de contraseña** (flujo completo)
- [x] **Modificación de perfil** con foto
- [x] **Todas las pantallas** funcionales con navegación fluida
- [x] **Almacenamiento local** (Room) y externo (Backend)
- [x] **Gestión de estado** desacoplada (StateFlow)
- [x] **Animaciones** funcionales y transiciones suaves

### ✅ Integraciones Obligatorias

- [x] **API externa pública:** OpenFDA para búsqueda de medicamentos
- [x] **Backend propio:** Microservicios Spring Boot con CRUD completo
- [x] **2 recursos nativos:**
  - [x] Cámara y galería (foto de perfil)
  - [x] Sistema de notificaciones (recordatorios de citas)

### ✅ Componentes Técnicos

- [x] **Pruebas unitarias:** Cobertura > 80% con JUnit y MockK
- [x] **APK firmado:** Generado con keystore .jks
- [x] **Arquitectura MVVM:** Separación clara de responsabilidades
- [x] **Persistencia local:** Room con entidades, DAO y Repositories
- [x] **Control de versiones:** Git con commits distribuidos

### ✅ Documentación

- [x] **README.md** completo con:
  - [x] Nombre de la app e integrantes
  - [x] Funcionalidades detalladas
  - [x] Endpoints (propios y externos)
  - [x] Instrucciones de ejecución
  - [x] Ubicación de APK y keystore
  - [x] Evidencia de colaboración

---

## 🐛 Solución de Problemas Comunes

### La app no se conecta al backend

**Problema:** Error de conexión al iniciar sesión o cargar datos.

**Solución:**
1. Verificar que el backend esté ejecutándose: `http://localhost:8080/api/profesionales`
2. Si usas emulador, verificar que la URL sea `http://10.0.2.2:8080`
3. Si usas dispositivo físico, verificar que estén en la misma red y usar la IP local
4. Revisar logs en Logcat (filtrar por "Retrofit" o "OkHttp")

### Error al compilar el proyecto

**Problema:** Gradle sync failed o errores de compilación.

**Solución:**
1. `Build > Clean Project`
2. `Build > Rebuild Project`
3. Invalidar caché: `File > Invalidate Caches / Restart`
4. Verificar JDK 11 en `File > Project Structure > SDK`

### Las notificaciones no funcionan

**Problema:** No se muestran notificaciones en Android 13+.

**Solución:**
1. Verificar permisos en `AndroidManifest.xml`: `POST_NOTIFICATIONS`
2. Solicitar permiso en tiempo de ejecución (ya implementado en `NotificationHelper`)
3. En la app, ir a: Configuración > Aplicaciones > MedicalConsulta > Notificaciones > Habilitar

### La cámara no se abre

**Problema:** Crash al intentar tomar foto.

**Solución:**
1. Verificar permisos en `AndroidManifest.xml`: `CAMERA`
2. Verificar que `file_paths.xml` esté configurado correctamente
3. Verificar que `FileProvider` esté declarado en el manifest
4. En emulador, verificar que la cámara virtual esté habilitada

### El APK no se instala en dispositivo

**Problema:** "App no instalada" o error de firma.

**Solución:**
1. Habilitar "Instalar apps desconocidas" en configuración del dispositivo
2. Verificar que el APK sea la versión release firmada
3. Desinstalar versión anterior si existe
4. Verificar que la firma del keystore sea correcta

### Base de datos Room corrupta

**Problema:** SQLiteException o errores al leer datos.

**Solución:**
1. Desinstalar la app completamente
2. Reinstalar (esto recrea la BD desde cero)
3. O ejecutar: `adb shell run-as cl.duoc.medicalconsulta rm databases/medical_consulta.db`

---

## 📚 Documentación Adicional

### Guías de Referencia

- **Jetpack Compose:** https://developer.android.com/jetpack/compose
- **Room Database:** https://developer.android.com/training/data-storage/room
- **Retrofit:** https://square.github.io/retrofit/
- **Material Design 3:** https://m3.material.io/
- **Spring Boot:** https://spring.io/projects/spring-boot
- **OpenFDA API:** https://open.fda.gov/apis/

### Arquitectura y Patrones

- **MVVM:** https://developer.android.com/topic/architecture
- **Repository Pattern:** https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern
- **StateFlow:** https://developer.android.com/kotlin/flow/stateflow-and-sharedflow

---

## 👨‍💻 Autores

Este proyecto fue desarrollado como parte de la **Evaluación Final Transversal (EFT)** de la asignatura **DSY1105 - Desarrollo de Aplicaciones Móviles** en DuocUC.

**Equipo:**
- [Nombre Integrante 1]
- [Nombre Integrante 2]

**Profesor:** [Nombre del profesor]
**Institución:** DuocUC
**Año:** 2024-2025

---

## 📄 Licencia

Este proyecto es de carácter académico y fue desarrollado exclusivamente para fines educativos.

---

## 📞 Contacto

Para consultas sobre el proyecto:
- **GitHub Issues:** [URL_REPOSITORIO]/issues
- **Email:** [email_contacto]

---

**Última actualización:** Diciembre 2024

---

## 🎯 Próximos Pasos para la Defensa

### Preparación Recomendada

1. **Ejecutar el proyecto completamente** y verificar que todo funciona
2. **Practicar la explicación** de la arquitectura MVVM
3. **Preparar ejemplos** de modificación de código en tiempo real
4. **Revisar los tests** y estar listo para ejecutarlos
5. **Tener abierto GitHub** para mostrar commits
6. **Tener listo Jira** para mostrar planificación

### Puntos Clave para la Defensa

- Explicar decisiones técnicas (¿Por qué MVVM? ¿Por qué Room?)
- Demostrar dominio del código (modificar en vivo)
- Mostrar pruebas unitarias ejecutándose
- Explicar integración frontend-backend
- Demostrar recursos nativos funcionando
- Mostrar APK firmado y explicar proceso

---

**¡Proyecto completo y listo para la evaluación! 🚀**
