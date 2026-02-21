🗓️ Sistema de Gestión de Turnos - v1.0

Gestión administrativa Full-Stack diseñada para optimizar la agenda de servicios profesionales. El sistema permite administrar clientes, staff y citas bajo una arquitectura escalable y segura.



Panel admin o profesional en después del login, sino para los clientes esta el /reservar para que reserven un turno y en panel admin se confirma



🚀 Funcionalidades Clave

🔑 Control de Acceso (RBAC)

Panel Admin: Control total de clientes, profesionales y turnos.



Panel Profesional: Visualización y gestión operativa de su propia agenda.



🗄️ Configuración de Base de Datos

El proyecto está configurado por defecto para H2



PostgreSQL: He dejado un archivo preparado (application-postgres.properties o el bloque comentado en el original) para que el sistema sea compatible con este motor simplemente cambiando las credenciales.



H2 Database: Ideal para pruebas rápidas sin necesidad de instalar un servidor local.



📅 Gestión de Agenda

Validación de Conflictos: Lógica de negocio para prevenir solapamiento de horarios.



Ciclo de Vida del Turno: Manejo de estados: Pendiente, Confirmado, Cancelado y Completado.



Filtros Avanzados: Búsqueda dinámica por fecha, estado y profesional con paginación desde el servidor.



👤 Administración de Datos

CRUD completo de Clientes y Profesionales.



Asignación dinámica de usuarios a perfiles de staff.



🧠 Decisiones de Arquitectura

Desacoplamiento con DTOs: Uso de Data Transfer Objects para proteger las entidades del dominio y optimizar la transferencia de datos.



Manejo Global de Excepciones: Respuestas controladas para errores de negocio (ej: Turno no encontrado, horario ocupado).



Paginación en Servidor: Implementación de Pageable para asegurar el rendimiento con grandes volúmenes de datos.

