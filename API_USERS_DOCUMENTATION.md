# 📚 Documentación de API - Gestión de Usuarios

Esta documentación describe todos los endpoints disponibles para la gestión de usuarios/clientes en el backend de Surequinos.

**Base URL:** `http://localhost:8080/api`

**Versión de API:** 2.0.0

---

## 📋 Tabla de Contenidos

1. [Información General](#información-general)
2. [Modelos de Datos](#modelos-de-datos)
3. [Endpoints GET](#-endpoints-get)
4. [Endpoints POST](#-endpoints-post)
5. [Endpoints PUT](#-endpoints-put)
6. [Endpoints DELETE](#-endpoints-delete)
7. [Flujos de Trabajo Comunes](#flujos-de-trabajo-comunes)
8. [Ejemplos de Integración](#ejemplos-de-integración)
9. [Manejo de Errores](#-manejo-de-errores)

---

## 📖 Información General

### Autenticación
Actualmente, los endpoints no requieren autenticación. Esto puede cambiar en futuras versiones.

### Formato de Respuesta
Todas las respuestas exitosas retornan datos en formato JSON.

### Códigos de Estado HTTP
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Recurso eliminado exitosamente
- `400 Bad Request`: Datos de entrada inválidos
- `404 Not Found`: Recurso no encontrado
- `409 Conflict`: Conflicto (ej: email o documento duplicado)
- `500 Internal Server Error`: Error interno del servidor

---

## 📦 Modelos de Datos

### UserDto (Respuesta)
Representa un usuario completo con toda su información.

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "roleId": "223e4567-e89b-12d3-a456-426614174001",
  "role": "CLIENTE",
  "documentNumber": "1234567890",
  "status": "ACTIVE",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00"
}
```

**Campos:**
- `id` (UUID): ID único del usuario
- `name` (String): Nombre completo del usuario
- `email` (String): Correo electrónico (único)
- `phoneNumber` (String, opcional): Número de teléfono celular
- `roleId` (UUID): ID del rol asignado
- `role` (UserRole enum): Rol del usuario (`ADMIN` o `CLIENTE`)
- `documentNumber` (String, opcional): Número de documento de identidad
- `status` (UserStatus enum): Estado del usuario. Valores:
  - `ACTIVE`: Usuario activo (por defecto)
  - `INACTIVE`: Usuario inactivo
  - `DELETED`: Usuario eliminado (soft delete)
- `createdAt` (DateTime): Fecha de creación
- `updatedAt` (DateTime): Fecha de última actualización

### CreateUserRequest (Request)
Datos necesarios para crear o actualizar un usuario.

```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "password": "miPassword123",
  "role": "CLIENTE",
  "documentNumber": "1234567890"
}
```

**Campos:**
- `name` (String, requerido): Nombre completo del usuario
- `email` (String, requerido): Correo electrónico (debe ser válido y único)
- `phoneNumber` (String, opcional): Número de teléfono celular
- `password` (String, requerido): Contraseña del usuario (se encripta automáticamente)
- `role` (UserRole enum, requerido): Rol del usuario. Valores permitidos:
  - `ADMIN`: Administrador del sistema
  - `CLIENTE`: Cliente que puede realizar compras
- `documentNumber` (String, opcional): Número de documento de identidad (debe ser único si se proporciona)

**Validaciones:**
- `name`: No puede estar vacío
- `email`: Debe ser un email válido y único en el sistema
- `password`: No puede estar vacío (se encripta con BCrypt antes de guardar)
- `role`: Debe ser `ADMIN` o `CLIENTE`
- `documentNumber`: Si se proporciona, debe ser único en el sistema

---

## 📥 ENDPOINTS GET

### 1. Obtener Todos los Usuarios

**Endpoint:** `GET /users`

**Descripción:** Retorna una lista de todos los usuarios registrados en el sistema.

**Parámetros:** Ninguno

**Ejemplo de Request:**
```http
GET /api/users
```

**cURL Ejemplo:**
```bash
curl http://localhost:8080/api/users
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "phoneNumber": "+57 300 1234567",
    "roleId": "223e4567-e89b-12d3-a456-426614174001",
    "role": "CLIENTE",
    "documentNumber": "1234567890",
    "createdAt": "2024-11-15T10:30:00",
    "updatedAt": "2024-11-15T10:30:00"
  },
  {
    "id": "323e4567-e89b-12d3-a456-426614174002",
    "name": "María García",
    "email": "maria.garcia@example.com",
    "phoneNumber": "+57 301 9876543",
    "roleId": "223e4567-e89b-12d3-a456-426614174001",
    "role": "CLIENTE",
    "documentNumber": "9876543210",
    "createdAt": "2024-11-14T09:20:00",
    "updatedAt": "2024-11-14T09:20:00"
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Usuarios obtenidos exitosamente (puede retornar array vacío si no hay usuarios activos)
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Solo se retornan usuarios con status `ACTIVE` o `INACTIVE`
- Los usuarios con status `DELETED` no aparecen en los resultados

---

### 2. Obtener Usuario por ID

**Endpoint:** `GET /users/{id}`

**Descripción:** Busca un usuario específico por su ID único (UUID).

**Parámetros de Path:**
- `id` (UUID, requerido): ID único del usuario

**Ejemplo de Request:**
```http
GET /api/users/123e4567-e89b-12d3-a456-426614174000
```

**cURL Ejemplo:**
```bash
curl http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "roleId": "223e4567-e89b-12d3-a456-426614174001",
  "role": "CLIENTE",
  "documentNumber": "1234567890",
  "status": "ACTIVE",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00"
}
```

**Códigos de Respuesta:**
- `200 OK`: Usuario encontrado
- `404 Not Found`: Usuario no encontrado o eliminado
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Solo se retornan usuarios con status `ACTIVE` o `INACTIVE`
- Los usuarios con status `DELETED` retornan `404 Not Found`

---

### 3. Obtener Usuario por Email

**Endpoint:** `GET /users/email/{email}`

**Descripción:** Busca un usuario específico por su correo electrónico.

**Parámetros de Path:**
- `email` (String, requerido): Email del usuario

**Ejemplo de Request:**
```http
GET /api/users/email/juan.perez@example.com
```

**cURL Ejemplo:**
```bash
curl http://localhost:8080/api/users/email/juan.perez@example.com
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "roleId": "223e4567-e89b-12d3-a456-426614174001",
  "role": "CLIENTE",
  "documentNumber": "1234567890",
  "status": "ACTIVE",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00"
}
```

**Códigos de Respuesta:**
- `200 OK`: Usuario encontrado
- `404 Not Found`: Usuario no encontrado o eliminado
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Solo se retornan usuarios con status `ACTIVE` o `INACTIVE`
- Los usuarios con status `DELETED` retornan `404 Not Found`

**Notas Importantes:**
- El email debe coincidir exactamente (case-sensitive)
- El email debe estar codificado en la URL si contiene caracteres especiales

---

### 4. Búsqueda Unificada de Usuarios

**Endpoint:** `GET /users/search`

**Descripción:** Endpoint unificado para buscar usuarios con todos los filtros posibles. Todos los parámetros son opcionales y se combinan con AND (todos deben cumplirse). Este es el endpoint principal para todas las búsquedas de usuarios, permitiendo filtrar por texto, roles, estados y rango de fechas simultáneamente.

**Parámetros de Query:**
- `name` (String, opcional): Nombre del usuario (búsqueda parcial, case-insensitive)
- `email` (String, opcional): Email del usuario (búsqueda parcial, case-insensitive)
- `documentNumber` (String, opcional): Número de documento del usuario (búsqueda parcial)
- `phoneNumber` (String, opcional): Número de teléfono del usuario (búsqueda parcial)
- `roles` (List<UserRole>, opcional): Rol(es) del usuario. Múltiples valores separados por comas o múltiples parámetros. Valores: `ADMIN`, `CLIENTE`. Filtro OR (cualquiera de los roles)
- `statuses` (List<UserStatus>, opcional): Estado(s) del usuario. Múltiples valores separados por comas o múltiples parámetros. Valores: `ACTIVE`, `INACTIVE`, `DELETED`. Filtro OR (cualquiera de los estados)
- `startDate` (String, opcional): Fecha de inicio del rango de creación (ISO 8601: `YYYY-MM-DDTHH:mm:ss`)
- `endDate` (String, opcional): Fecha de fin del rango de creación (ISO 8601: `YYYY-MM-DDTHH:mm:ss`)

**Ejemplos de Request:**

```http
# Buscar por nombre
GET /api/users/search?name=Juan

# Buscar por email
GET /api/users/search?email=juan@example.com

# Buscar por número de documento
GET /api/users/search?documentNumber=1234567890

# Buscar por teléfono
GET /api/users/search?phoneNumber=3001234567

# Solo clientes activos
GET /api/users/search?roles=CLIENTE&statuses=ACTIVE

# Clientes activos e inactivos (excluyendo eliminados)
GET /api/users/search?roles=CLIENTE&statuses=ACTIVE,INACTIVE

# Solo administradores
GET /api/users/search?roles=ADMIN

# Administradores y clientes activos
GET /api/users/search?roles=ADMIN,CLIENTE&statuses=ACTIVE

# Clientes creados en noviembre 2024
GET /api/users/search?roles=CLIENTE&startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59

# Buscar por nombre y rol (clientes activos)
GET /api/users/search?name=Juan&roles=CLIENTE&statuses=ACTIVE

# Todos los usuarios inactivos
GET /api/users/search?statuses=INACTIVE

# Buscar todos los clientes con nombre que contenga "María" y que estén activos
GET /api/users/search?name=María&roles=CLIENTE&statuses=ACTIVE
```

**cURL Ejemplos:**
```bash
# Buscar por nombre
curl "http://localhost:8080/api/users/search?name=Juan"

# Buscar por email
curl "http://localhost:8080/api/users/search?email=juan@example.com"

# Solo clientes activos
curl "http://localhost:8080/api/users/search?roles=CLIENTE&statuses=ACTIVE"

# Clientes activos e inactivos
curl "http://localhost:8080/api/users/search?roles=CLIENTE&statuses=ACTIVE,INACTIVE"

# Administradores creados en noviembre
curl "http://localhost:8080/api/users/search?roles=ADMIN&startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59"

# Búsqueda combinada
curl "http://localhost:8080/api/users/search?name=Juan&roles=CLIENTE&statuses=ACTIVE"
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "phoneNumber": "+57 300 1234567",
    "roleId": "223e4567-e89b-12d3-a456-426614174001",
    "role": "CLIENTE",
    "documentNumber": "1234567890",
    "createdAt": "2024-11-15T10:30:00",
    "updatedAt": "2024-11-15T10:30:00"
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Usuarios encontrados exitosamente (puede retornar array vacío si no hay coincidencias)
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- **Todos los parámetros son opcionales**: Si no se proporciona ningún parámetro, se retornan todos los usuarios (incluyendo eliminados si no se filtra por status)
- **Filtro por defecto**: Si no especificas `statuses`, **NO se excluyen automáticamente los eliminados**. Debes especificar explícitamente `statuses=ACTIVE,INACTIVE` si quieres excluir eliminados
- **Filtros AND**: Todos los parámetros proporcionados se combinan con AND (todos deben cumplirse)
- **Filtros OR para roles y estados**: 
  - `roles=ADMIN,CLIENTE` retorna usuarios que tengan **cualquiera** de esos roles
  - `statuses=ACTIVE,INACTIVE` retorna usuarios que tengan **cualquiera** de esos estados
- **Rango de fechas**: 
  - Formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`
  - Si solo se proporciona `startDate`, se buscan usuarios desde esa fecha hasta ahora
  - Si solo se proporciona `endDate`, se buscan usuarios desde siempre hasta esa fecha
  - Si se proporcionan ambas, se valida que `startDate <= endDate`
- **Búsquedas de texto**: Son parciales (LIKE) - no es necesario el texto completo
- **Case-insensitive**: Las búsquedas de nombre y email no distinguen mayúsculas/minúsculas
- **Ordenamiento**: Los resultados están ordenados por fecha de creación descendente (más recientes primero)
- **Roles y estados**: Deben ser exactamente `ADMIN` o `CLIENTE` para roles, y `ACTIVE`, `INACTIVE` o `DELETED` para estados (case-sensitive)

**Casos de Uso Comunes:**
- **Sección de Clientes (solo activos)**: `?roles=CLIENTE&statuses=ACTIVE`
- **Sección de Usuarios/Administradores**: `?roles=ADMIN&statuses=ACTIVE,INACTIVE`
- **Todos los usuarios activos**: `?statuses=ACTIVE`
- **Clientes inactivos**: `?roles=CLIENTE&statuses=INACTIVE`
- **Reporte mensual de clientes**: `?roles=CLIENTE&startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59`

---

## ✏️ ENDPOINTS POST

### 1. Crear Nuevo Usuario

**Endpoint:** `POST /users`

**Descripción:** Crea un nuevo usuario/cliente en el sistema. El rol se especifica usando el enum UserRole (`ADMIN` o `CLIENTE`). La contraseña se encripta automáticamente con BCrypt antes de guardarse.

**Body (JSON):**
```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "password": "miPassword123",
  "role": "CLIENTE",
  "documentNumber": "1234567890"
}
```

**Ejemplo de Request:**
```http
POST /api/users
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "password": "miPassword123",
  "role": "CLIENTE",
  "documentNumber": "1234567890"
}
```

**cURL Ejemplo:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "phoneNumber": "+57 300 1234567",
    "password": "miPassword123",
    "role": "CLIENTE",
    "documentNumber": "1234567890"
  }'
```

**Respuesta Exitosa (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Juan Pérez",
  "email": "juan.perez@example.com",
  "phoneNumber": "+57 300 1234567",
  "roleId": "223e4567-e89b-12d3-a456-426614174001",
  "role": "CLIENTE",
  "documentNumber": "1234567890",
  "status": "ACTIVE",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00"
}
```

**Códigos de Respuesta:**
- `201 Created`: Usuario creado exitosamente
- `400 Bad Request`: Datos de entrada inválidos (validaciones fallidas)
- `409 Conflict`: Ya existe un usuario con el mismo email o documento
- `500 Internal Server Error`: Error interno del servidor

**Validaciones:**
- `name`: No puede estar vacío
- `email`: Debe ser un email válido y único en el sistema
- `password`: No puede estar vacío (se encripta automáticamente)
- `role`: Debe ser `ADMIN` o `CLIENTE`
- `documentNumber`: Si se proporciona, debe ser único en el sistema

**Notas Importantes:**
- La contraseña se encripta automáticamente con BCrypt antes de guardarse
- El email debe ser único en el sistema (solo entre usuarios activos)
- El número de documento debe ser único si se proporciona (solo entre usuarios activos)
- El rol se crea automáticamente si no existe en la base de datos
- **Reactivación automática**: Si se intenta crear un usuario con un email o documento que pertenece a un usuario eliminado (status = DELETED), el sistema automáticamente reactivará ese usuario y actualizará sus datos con la información proporcionada
- El status se establece automáticamente como `ACTIVE` para nuevos usuarios

---

## 🔄 ENDPOINTS PUT

### 1. Actualizar Usuario Existente

**Endpoint:** `PUT /users/{id}`

**Descripción:** Actualiza los datos de un usuario existente. Todos los campos se actualizan con los valores proporcionados. **La contraseña es opcional**: si se envía vacía o no se proporciona, se mantiene la contraseña actual del usuario. Si se proporciona una nueva contraseña, se encripta automáticamente antes de guardarse.

**Parámetros de Path:**
- `id` (UUID, requerido): ID único del usuario a actualizar

**Body (JSON):**
```json
{
  "name": "Juan Pérez Actualizado",
  "email": "juan.perez.nuevo@example.com",
  "phoneNumber": "+57 301 9876543",
  "password": "nuevaPassword123",
  "role": "ADMIN",
  "documentNumber": "1234567890"
}
```

**Nota sobre la contraseña:** El campo `password` es opcional. Si se envía vacío (`""`), `null` o se omite, se mantiene la contraseña actual del usuario. Si se proporciona un valor, se actualiza la contraseña.

**Ejemplo de Request:**
```http
PUT /api/users/123e4567-e89b-12d3-a456-426614174000
Content-Type: application/json

{
  "name": "Juan Pérez Actualizado",
  "email": "juan.perez.nuevo@example.com",
  "phoneNumber": "+57 301 9876543",
  "password": "nuevaPassword123",
  "role": "ADMIN",
  "documentNumber": "1234567890"
}
```

**cURL Ejemplo:**
```bash
curl -X PUT http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez Actualizado",
    "email": "juan.perez.nuevo@example.com",
    "phoneNumber": "+57 301 9876543",
    "password": "nuevaPassword123",
    "role": "ADMIN",
    "documentNumber": "1234567890"
  }'
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Juan Pérez Actualizado",
  "email": "juan.perez.nuevo@example.com",
  "phoneNumber": "+57 301 9876543",
  "roleId": "423e4567-e89b-12d3-a456-426614174003",
  "role": "ADMIN",
  "documentNumber": "1234567890",
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T11:45:00"
}
```

**Códigos de Respuesta:**
- `200 OK`: Usuario actualizado exitosamente
- `400 Bad Request`: Datos de entrada inválidos o usuario no encontrado
- `404 Not Found`: Usuario no encontrado
- `409 Conflict`: Ya existe un usuario con el mismo email o documento
- `500 Internal Server Error`: Error interno del servidor

**Validaciones:**
- El usuario debe existir (ID válido)
- `name`: No puede estar vacío
- `email`: Debe ser un email válido y único en el sistema (excluyendo el usuario actual)
- `password`: **Opcional**. Si se proporciona, se encripta automáticamente. Si está vacío o se omite, se mantiene la contraseña actual
- `role`: Debe ser `ADMIN` o `CLIENTE`
- `documentNumber`: Si se proporciona, debe ser único en el sistema (excluyendo el usuario actual)

**Notas Importantes:**
- Todos los campos se actualizan, excepto la contraseña si no se proporciona
- **Contraseña opcional**: Si el campo `password` está vacío, `null` o se omite, se mantiene la contraseña actual del usuario
- Si se proporciona una nueva contraseña, se encripta automáticamente con BCrypt
- El email y documento deben ser únicos, pero se excluye el usuario actual de la validación

---

## 🗑️ ENDPOINTS DELETE

### 1. Eliminar Usuario (Soft Delete)

**Endpoint:** `DELETE /users/{id}`

**Descripción:** Elimina un usuario existente del sistema mediante soft delete. El usuario no se elimina físicamente de la base de datos, sino que se marca con status `DELETED`. Los usuarios eliminados no aparecen en ninguna consulta GET, pero pueden ser reactivados automáticamente si se intenta crear un nuevo usuario con el mismo email o documento.

**Parámetros de Path:**
- `id` (UUID, requerido): ID único del usuario a eliminar

**Ejemplo de Request:**
```http
DELETE /api/users/123e4567-e89b-12d3-a456-426614174000
```

**cURL Ejemplo:**
```bash
curl -X DELETE http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000
```

**Respuesta Exitosa (204 No Content):**
```
(No body)
```

**Códigos de Respuesta:**
- `204 No Content`: Usuario marcado como eliminado exitosamente
- `404 Not Found`: Usuario no encontrado o ya eliminado
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- **Soft Delete**: La eliminación no es permanente. El usuario se marca con `status = DELETED`
- Los usuarios eliminados (`DELETED`) no aparecen en ninguna consulta GET (getAllUsers, getUserById, getUserByEmail, searchUsers)
- **Reactivación automática**: Si se intenta crear un usuario con el mismo email o documento de un usuario eliminado, el sistema automáticamente reactivará ese usuario y actualizará sus datos
- El usuario eliminado mantiene su ID y relaciones con órdenes, pero no es visible en las consultas normales

---

## 🔄 Flujos de Trabajo Comunes

### Flujo 1: Crear y Gestionar un Nuevo Cliente
```
1. POST /users - Crear nuevo cliente
2. GET /users/{id} - Verificar que se creó correctamente
3. PUT /users/{id} - Actualizar datos si es necesario
4. GET /users/search?email={email} - Buscar por email
```

### Flujo 2: Buscar Cliente por Email o Documento
```
1. GET /users/email/{email} - Buscar por email exacto
2. GET /users/search?documentNumber={doc} - Buscar por documento
3. GET /users/search?name={nombre} - Buscar por nombre parcial
```

### Flujo 3: Búsqueda Unificada de Usuarios
```
1. GET /users/search?roles=CLIENTE&statuses=ACTIVE - Solo clientes activos (sección de clientes)
2. GET /users/search?roles=ADMIN&statuses=ACTIVE,INACTIVE - Solo administradores activos e inactivos
3. GET /users/search?roles=CLIENTE&statuses=INACTIVE - Clientes inactivos
4. GET /users/search?roles=CLIENTE&startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59 - Clientes creados en noviembre
5. GET /users/search?name=Juan&roles=CLIENTE&statuses=ACTIVE - Búsqueda combinada
6. GET /users/search?roles=CLIENTE - Solo por rol (sin otros filtros)
```

### Flujo 4: Actualizar Datos de Usuario
```
1. GET /users/{id} - Obtener datos actuales
2. PUT /users/{id} - Actualizar con nuevos datos
3. GET /users/{id} - Verificar cambios
```

### Flujo 5: Eliminar y Reactivar Usuario
```
1. GET /users/{id} - Verificar que existe
2. DELETE /users/{id} - Eliminar usuario (soft delete)
3. GET /users/{id} - Verificar que fue eliminado (debe retornar 404)
4. POST /users - Crear usuario con mismo email/documento (se reactiva automáticamente)
5. GET /users/{id} - Verificar que fue reactivado
```

---

## ⚠️ Manejo de Errores

### Errores Comunes

**400 Bad Request - Datos Inválidos**
```json
{
  "timestamp": "2024-11-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada inválidos"
}
```

**404 Not Found - Usuario No Encontrado**
```json
{
  "timestamp": "2024-11-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario no encontrado"
}
```

**409 Conflict - Email o Documento Duplicado**
```json
{
  "timestamp": "2024-11-15T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un usuario con el email: juan.perez@example.com"
}
```

**500 Internal Server Error**
```json
{
  "timestamp": "2024-11-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error interno del servidor"
}
```

---

## 💻 Ejemplos de Integración

### JavaScript/TypeScript (Fetch API)

```typescript
const API_BASE_URL = 'http://localhost:8080/api';

// Obtener todos los usuarios
async function getAllUsers() {
  const response = await fetch(`${API_BASE_URL}/users`);
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Obtener usuario por ID
async function getUserById(id: string) {
  const response = await fetch(`${API_BASE_URL}/users/${id}`);
  if (response.ok) {
    return await response.json();
  }
  if (response.status === 404) {
    return null;
  }
  throw new Error(`Error: ${response.status}`);
}

// Obtener usuario por email
async function getUserByEmail(email: string) {
  const encodedEmail = encodeURIComponent(email);
  const response = await fetch(`${API_BASE_URL}/users/email/${encodedEmail}`);
  if (response.ok) {
    return await response.json();
  }
  if (response.status === 404) {
    return null;
  }
  throw new Error(`Error: ${response.status}`);
}

// Búsqueda unificada de usuarios
async function searchUsers(filters: {
  name?: string;
  email?: string;
  documentNumber?: string;
  phoneNumber?: string;
  roles?: ('ADMIN' | 'CLIENTE')[];
  statuses?: ('ACTIVE' | 'INACTIVE' | 'DELETED')[];
  startDate?: string;
  endDate?: string;
}) {
  const params = new URLSearchParams();
  if (filters.name) params.append('name', filters.name);
  if (filters.email) params.append('email', filters.email);
  if (filters.documentNumber) params.append('documentNumber', filters.documentNumber);
  if (filters.phoneNumber) params.append('phoneNumber', filters.phoneNumber);
  if (filters.roles) {
    filters.roles.forEach(role => params.append('roles', role));
  }
  if (filters.statuses) {
    filters.statuses.forEach(status => params.append('statuses', status));
  }
  if (filters.startDate) params.append('startDate', filters.startDate);
  if (filters.endDate) params.append('endDate', filters.endDate);
  
  const response = await fetch(
    `${API_BASE_URL}/users/search?${params.toString()}`
  );
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Ejemplos de uso:
// searchUsers({ roles: ['CLIENTE'], statuses: ['ACTIVE'] }) // Solo clientes activos
// searchUsers({ roles: ['ADMIN'], statuses: ['ACTIVE', 'INACTIVE'] }) // Administradores activos e inactivos
// searchUsers({ roles: ['CLIENTE'], startDate: '2024-11-01T00:00:00', endDate: '2024-11-30T23:59:59' }) // Clientes de noviembre

// Crear nuevo usuario
async function createUser(userData: {
  name: string;
  email: string;
  phoneNumber?: string;
  password: string;
  role: 'ADMIN' | 'CLIENTE';
  documentNumber?: string;
}) {
  const response = await fetch(`${API_BASE_URL}/users`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });
  if (response.ok || response.status === 201) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Actualizar usuario (password es opcional)
async function updateUser(id: string, userData: {
  name: string;
  email: string;
  phoneNumber?: string;
  password?: string; // Opcional: si está vacío, se mantiene la actual
  role: 'ADMIN' | 'CLIENTE';
  documentNumber?: string;
}) {
  const response = await fetch(`${API_BASE_URL}/users/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData),
  });
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Eliminar usuario
async function deleteUser(id: string) {
  const response = await fetch(`${API_BASE_URL}/users/${id}`, {
    method: 'DELETE',
  });
  if (response.ok || response.status === 204) {
    return true;
  }
  throw new Error(`Error: ${response.status}`);
}
```

### Axios
```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Obtener todos los usuarios
const getAllUsers = async () => {
  const response = await api.get('/users');
  return response.data;
};

// Obtener usuario por ID
const getUserById = async (id: string) => {
  try {
    const response = await api.get(`/users/${id}`);
    return response.data;
  } catch (error: any) {
    if (error.response?.status === 404) {
      return null;
    }
    throw error;
  }
};

// Obtener usuario por email
const getUserByEmail = async (email: string) => {
  try {
    const response = await api.get(`/users/email/${encodeURIComponent(email)}`);
    return response.data;
  } catch (error: any) {
    if (error.response?.status === 404) {
      return null;
    }
    throw error;
  }
};

// Búsqueda unificada de usuarios
const searchUsers = async (filters: {
  name?: string;
  email?: string;
  documentNumber?: string;
  phoneNumber?: string;
  roles?: ('ADMIN' | 'CLIENTE')[];
  statuses?: ('ACTIVE' | 'INACTIVE' | 'DELETED')[];
  startDate?: string;
  endDate?: string;
}) => {
  const response = await api.get('/users/search', {
    params: filters
  });
  return response.data;
};

// Ejemplos de uso:
// searchUsers({ roles: ['CLIENTE'], statuses: ['ACTIVE'] }) // Solo clientes activos
// searchUsers({ roles: ['ADMIN'], statuses: ['ACTIVE', 'INACTIVE'] }) // Administradores activos e inactivos
// searchUsers({ roles: ['CLIENTE'], startDate: '2024-11-01T00:00:00', endDate: '2024-11-30T23:59:59' }) // Clientes de noviembre

// Crear nuevo usuario
const createUser = async (userData: {
  name: string;
  email: string;
  phoneNumber?: string;
  password: string;
  role: 'ADMIN' | 'CLIENTE';
  documentNumber?: string;
}) => {
  const response = await api.post('/users', userData);
  return response.data;
};

// Actualizar usuario (password es opcional)
const updateUser = async (id: string, userData: {
  name: string;
  email: string;
  phoneNumber?: string;
  password?: string; // Opcional: si está vacío, se mantiene la actual
  role: 'ADMIN' | 'CLIENTE';
  documentNumber?: string;
}) => {
  const response = await api.put(`/users/${id}`, userData);
  return response.data;
};

// Eliminar usuario
const deleteUser = async (id: string) => {
  await api.delete(`/users/${id}`);
  return true;
};
```

---

**Última actualización:** 2024-11-15

