🩺 appMedicalConsulta – Sistema de Reservas Médicas

Proyecto académico – Evaluación Parcial 2 – DSY1105
Desarrollado en Kotlin (Android Studio)
Metodología: Scrum / Gestión en Jira

📘 Descripción general

MediTime App es una aplicación móvil desarrollada en Kotlin que permite gestionar reservas y atenciones médicas, brindando una interfaz simple para pacientes y profesionales de la salud.
El proyecto corresponde al Caso N°4 – Consultas médicas, reservas u horas de atención, propuesto en la evaluación parcial 2 del curso Desarrollo de Software y Servicios Web (DSY1105).

Esta primera versión corresponde al MVP (Producto Mínimo Viable), donde el usuario puede:

Registrarse e iniciar sesión.

Visualizar profesionales disponibles.

Agendar una cita médica.

Ver el historial de reservas.

Cerrar sesión de forma segura.

🎯 Objetivo general

Desarrollar un sistema funcional que permita agendar, consultar y gestionar citas médicas simuladas, priorizando la usabilidad, navegación fluida y validaciones básicas.

🧩 Funcionalidades principales (MVP)
Módulo	Funcionalidad	Descripción
👤 Usuarios	Registro e inicio de sesión	Validación de datos básicos y acceso a la app
🧑‍⚕️ Profesionales	Visualización de lista	Muestra profesionales disponibles (datos simulados)
📅 Agenda	Agendar cita	Selección de fecha, hora y profesional
📋 Historial	Ver citas agendadas	Muestra citas previas o pendientes
🔒 Sesión	Cierre de sesión	Finaliza sesión activa y limpia datos temporales
⚙️ Tecnologías utilizadas
Tecnología	Uso principal
Kotlin	Lenguaje de desarrollo principal
Android Studio	Entorno de desarrollo (IDE)
XML	Diseño de interfaz de usuario
GitHub	Control de versiones y colaboración
Jira	Gestión ágil de tareas (Scrum)
Firebase (futuro)	Persistencia de datos (planeado para iteración 2)

🧱 Estructura del proyecto
📦 AppMedicalConsulta
 ┣ 📂 app
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java/com/meditimeapp
 ┃ ┃ ┃ ┣ 📂 ui        → Interfaces gráficas (pantallas)
 ┃ ┃ ┃ ┣ 📂 data      → Clases simuladas (modelos)
 ┃ ┃ ┃ ┣ 📂 logic     → Lógica de reserva y validaciones
 ┃ ┃ ┣ 📂 res         → Recursos XML (layouts, strings, colores)
 ┣ 📄 README.md
 ┣ 📄 build.gradle
 ┗ 📄 settings.gradle

🧠 Historias de Usuario (HU)
Código	Título	Estado
HU-01	Registro de usuario	✅ MVP
HU-02	Inicio de sesión	✅ MVP
HU-03	Visualizar profesionales	✅ MVP
HU-04	Agendar cita	✅ MVP
HU-05	Ver historial de citas	✅ MVP
HU-06	Cerrar sesión	✅ MVP
HU-07	Modificar / cancelar cita	🔜 Iteración 2
HU-08	Perfil del profesional	🔜 Iteración 2
HU-09	Validaciones avanzadas	🔜 Iteración 2
HU-10	Panel del profesional	🔜 Iteración 3
🧭 Alcance del MVP

El MVP (Producto Mínimo Viable) cubre el flujo completo desde el registro de usuario hasta la reserva de cita médica y su visualización posterior, validando las interacciones clave sin necesidad de una base de datos real.

Incluye:

Navegación funcional entre pantallas.

Validaciones de campos obligatorios.

Datos simulados (mock data).

No incluye aún:

Cancelación de citas.

CRUD completo de profesionales.

Conexión a base de datos externa.
