📘 Descripción general

MedicalConsulta App es una aplicación móvil desarrollada en Kotlin que permite gestionar reservas y atenciones médicas, brindando una interfaz simple para pacientes y profesionales de la salud.
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

<img width="786" height="431" alt="image" src="https://github.com/user-attachments/assets/6cf6facf-72b3-467b-88ec-d4189e19b10e" />


<img width="755" height="571" alt="image" src="https://github.com/user-attachments/assets/a183c115-f699-466c-9f0b-b0452d5db568" />


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

