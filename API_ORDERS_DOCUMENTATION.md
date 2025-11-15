# Documentación API de Órdenes - Endpoints GET y PATCH

## Base URL
```
http://localhost:8080/api/orders
```

---

## 📋 Estructura de Datos

### OrderDto (Respuesta de Orden)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orderNumber": "ORD-20241115143022-456",
  "userId": "08c0a3f9-48d7-40f1-bc86-1e0e88755a55",
  "discountValue": 50000.00,
  "notes": "Entregar en horario laboral",
  "paymentStatus": "PENDING",
  "paymentMethod": "TARJETA_CREDITO",
  "shippingValue": 15000.00,
  "status": "PENDING",
  "subtotal": 1700000.00,
  "total": 1665000.00,
  "shippingAddress": "Calle 123 #45-67, Barrio Centro",
  "createdAt": "2024-11-15T14:30:22",
  "updatedAt": "2024-11-15T14:30:22",
  "userName": "Juan Pérez",
  "userEmail": "cliente@example.com",
  "orderItems": [
    {
      "id": "789e4567-e89b-12d3-a456-426614174000",
      "orderId": "123e4567-e89b-12d3-a456-426614174000",
      "variantId": "456e4567-e89b-12d3-a456-426614174000",
      "quantity": 2,
      "unitPrice": 850000.00,
      "totalPrice": 1700000.00,
      "createdAt": "2024-11-15T14:30:22",
      "variantSku": "SIL-NINO-ROBLE-12",
      "productName": "Zapatos Caballo"
    }
  ]
}
```

### OrderItemDto (Item de Orden)
```json
{
  "id": "789e4567-e89b-12d3-a456-426614174000",
  "orderId": "123e4567-e89b-12d3-a456-426614174000",
  "variantId": "456e4567-e89b-12d3-a456-426614174000",
  "quantity": 2,
  "unitPrice": 850000.00,
  "totalPrice": 1700000.00,
  "createdAt": "2024-11-15T14:30:22",
  "variantSku": "SIL-NINO-ROBLE-12",
  "productName": "Zapatos Caballo"
}
```

### Valores Permitidos

**Estados de Orden (`status`):**
- `PENDING` - Pendiente
- `CONFIRMED` - Confirmada
- `PROCESSING` - En proceso
- `SHIPPED` - Enviada
- `DELIVERED` - Entregada
- `CANCELLED` - Cancelada

**Estados de Pago (`paymentStatus`):**
- `PENDING` - Pendiente
- `PAID` - Pagado
- `FAILED` - Fallido
- `REFUNDED` - Reembolsado

**Métodos de Pago (`paymentMethod`):**
- `TARJETA_CREDITO` - Tarjeta de crédito
- `TRANSFERENCIA_BANCARIA` - Transferencia bancaria
- `EFECTIVO` - Pago en efectivo
- `CONTRAENTREGA` - Contra entrega (pago al recibir)
- `NEQUI` - Nequi
- `DAVIPLATA` - Daviplata

---

## 🔍 ENDPOINTS GET

### 1. Obtener Orden por ID

**Endpoint:** `GET /orders/{id}`

**Descripción:** Obtiene una orden específica por su ID único (UUID).

**Parámetros:**
- `id` (path, UUID, requerido): ID único de la orden

**Ejemplo de Request:**
```http
GET /api/orders/123e4567-e89b-12d3-a456-426614174000
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orderNumber": "ORD-20241115143022-456",
  "userId": "08c0a3f9-48d7-40f1-bc86-1e0e88755a55",
  "discountValue": 50000.00,
  "notes": "Entregar en horario laboral",
  "paymentStatus": "PENDING",
  "paymentMethod": "TARJETA_CREDITO",
  "shippingValue": 15000.00,
  "status": "PENDING",
  "subtotal": 1700000.00,
  "total": 1665000.00,
  "shippingAddress": "Calle 123 #45-67, Barrio Centro",
  "createdAt": "2024-11-15T14:30:22",
  "updatedAt": "2024-11-15T14:30:22",
  "userName": "Juan Pérez",
  "userEmail": "cliente@example.com",
  "orderItems": [...]
}
```

**Códigos de Respuesta:**
- `200 OK`: Orden encontrada
- `404 Not Found`: Orden no encontrada
- `500 Internal Server Error`: Error interno del servidor

---

### 2. Obtener Orden por Número de Orden

**Endpoint:** `GET /orders/number/{orderNumber}`

**Descripción:** Obtiene una orden específica por su número de orden único (formato: ORD-YYYYMMDDHHMMSS-XXX).

**Parámetros:**
- `orderNumber` (path, String, requerido): Número de orden único

**Ejemplo de Request:**
```http
GET /api/orders/number/ORD-20241115143022-456
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orderNumber": "ORD-20241115143022-456",
  ...
}
```

**Códigos de Respuesta:**
- `200 OK`: Orden encontrada
- `404 Not Found`: Orden no encontrada
- `500 Internal Server Error`: Error interno del servidor

---

### 3. Obtener Todas las Órdenes (con Paginación)

**Endpoint:** `GET /orders`

**Descripción:** Obtiene todas las órdenes con paginación y ordenamiento opcional.

**Parámetros de Query:**
- `page` (Integer, opcional): Número de página (inicia en 0). Default: 0
- `size` (Integer, opcional): Tamaño de página. Default: 20
- `sort` (String, opcional): Campo de ordenamiento. Formato: `campo,direccion`. Default: `createdAt,desc`

**Campos de Ordenamiento Válidos:**
- `createdAt` - Fecha de creación
- `updatedAt` - Fecha de actualización
- `orderNumber` - Número de orden
- `total` - Total de la orden
- `status` - Estado de la orden
- `paymentStatus` - Estado de pago

**Direcciones de Ordenamiento:**
- `asc` - Ascendente
- `desc` - Descendente

**Ejemplos de Request:**

```http
# Obtener primera página con valores por defecto
GET /api/orders

# Obtener página específica
GET /api/orders?page=0&size=10

# Ordenar por fecha de creación descendente
GET /api/orders?page=0&size=20&sort=createdAt,desc

# Ordenar por total ascendente
GET /api/orders?page=0&size=20&sort=total,asc

# Ordenar por estado
GET /api/orders?page=0&size=20&sort=status,asc
```

**Respuesta Exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "orderNumber": "ORD-20241115143022-456",
      ...
    },
    {
      "id": "223e4567-e89b-12d3-a456-426614174001",
      "orderNumber": "ORD-20241115143023-457",
      ...
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20,
  "empty": false
}
```

**Estructura de Respuesta Paginada:**
- `content`: Array de órdenes
- `totalElements`: Total de órdenes en la base de datos
- `totalPages`: Total de páginas
- `number`: Número de página actual (0-indexed)
- `size`: Tamaño de página
- `first`: Si es la primera página
- `last`: Si es la última página
- `numberOfElements`: Cantidad de elementos en la página actual

**Códigos de Respuesta:**
- `200 OK`: Órdenes obtenidas exitosamente
- `500 Internal Server Error`: Error interno del servidor

---

### 4. Obtener Órdenes de un Usuario

**Endpoint:** `GET /orders/user/{userId}`

**Descripción:** Obtiene todas las órdenes de un usuario específico (sin paginación).

**Parámetros:**
- `userId` (path, UUID, requerido): ID del usuario

**Ejemplo de Request:**
```http
GET /api/orders/user/08c0a3f9-48d7-40f1-bc86-1e0e88755a55
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "orderNumber": "ORD-20241115143022-456",
    "userId": "08c0a3f9-48d7-40f1-bc86-1e0e88755a55",
    ...
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "orderNumber": "ORD-20241115143023-457",
    "userId": "08c0a3f9-48d7-40f1-bc86-1e0e88755a55",
    ...
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Órdenes obtenidas exitosamente
- `500 Internal Server Error`: Error interno del servidor

---

### 5. Obtener Órdenes de un Usuario (con Paginación)

**Endpoint:** `GET /orders/user/{userId}/page`

**Descripción:** Obtiene las órdenes de un usuario específico con paginación y ordenamiento opcional.

**Parámetros:**
- `userId` (path, UUID, requerido): ID del usuario
- `page` (query, Integer, opcional): Número de página (inicia en 0). Default: 0
- `size` (query, Integer, opcional): Tamaño de página. Default: 20
- `sort` (query, String, opcional): Campo de ordenamiento. Default: `createdAt,desc`

**Ejemplo de Request:**
```http
GET /api/orders/user/08c0a3f9-48d7-40f1-bc86-1e0e88755a55/page?page=0&size=10&sort=createdAt,desc
```

**Respuesta Exitosa (200 OK):**
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "orderNumber": "ORD-20241115143022-456",
      ...
    }
  ],
  "pageable": {...},
  "totalElements": 25,
  "totalPages": 3,
  ...
}
```

**Códigos de Respuesta:**
- `200 OK`: Órdenes obtenidas exitosamente
- `500 Internal Server Error`: Error interno del servidor

---

### 6. Obtener Órdenes por Estado

**Endpoint:** `GET /orders/status/{status}`

**Descripción:** Obtiene todas las órdenes con un estado específico.

**Parámetros:**
- `status` (path, String, requerido): Estado de la orden

**Valores Válidos:**
- `PENDING`
- `CONFIRMED`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Ejemplo de Request:**
```http
GET /api/orders/status/CONFIRMED
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "orderNumber": "ORD-20241115143022-456",
    "status": "CONFIRMED",
    ...
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "orderNumber": "ORD-20241115143023-457",
    "status": "CONFIRMED",
    ...
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Órdenes obtenidas exitosamente
- `500 Internal Server Error`: Error interno del servidor

---

### 7. Obtener Órdenes por Estado de Pago

**Endpoint:** `GET /orders/payment-status/{paymentStatus}`

**Descripción:** Obtiene todas las órdenes con un estado de pago específico.

**Parámetros:**
- `paymentStatus` (path, String, requerido): Estado de pago

**Valores Válidos:**
- `PENDING`
- `PAID`
- `FAILED`
- `REFUNDED`

**Ejemplo de Request:**
```http
GET /api/orders/payment-status/PAID
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "orderNumber": "ORD-20241115143022-456",
    "paymentStatus": "PAID",
    ...
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "orderNumber": "ORD-20241115143023-457",
    "paymentStatus": "PAID",
    ...
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Órdenes obtenidas exitosamente
- `500 Internal Server Error`: Error interno del servidor

---

### 8. Buscar Órdenes (Búsqueda Avanzada)

**Endpoint:** `GET /orders/search`

**Descripción:** Busca órdenes por múltiples criterios. Todos los parámetros son opcionales y se combinan con AND (todos los criterios proporcionados deben cumplirse). Las búsquedas de texto son parciales (LIKE), lo que permite encontrar coincidencias parciales.

**Parámetros de Query (todos opcionales):**
- `orderId` (UUID, opcional): ID único de la orden (búsqueda exacta)
- `orderNumber` (String, opcional): Número de orden (búsqueda parcial)
- `clientName` (String, opcional): Nombre del cliente (búsqueda parcial, case-insensitive)
- `email` (String, opcional): Email del cliente (búsqueda parcial, case-insensitive)
- `documentNumber` (String, opcional): Número de documento del cliente (búsqueda parcial)
- `phoneNumber` (String, opcional): Número de teléfono del cliente (búsqueda parcial)

**Comportamiento:**
- Si no se proporciona ningún parámetro, retorna todas las órdenes
- Si se proporcionan múltiples parámetros, todos deben cumplirse (AND)
- Las búsquedas de texto son parciales: `"Juan"` encontrará "Juan Pérez", "Juan Carlos", etc.
- Las búsquedas de texto son case-insensitive para nombre y email

**Ejemplos de Request:**

```http
# Buscar por ID de orden (exacto)
GET /api/orders/search?orderId=123e4567-e89b-12d3-a456-426614174000

# Buscar por número de orden (parcial)
GET /api/orders/search?orderNumber=ORD-2024

# Buscar por nombre del cliente (parcial)
GET /api/orders/search?clientName=Juan

# Buscar por email (parcial)
GET /api/orders/search?email=cliente@example.com

# Buscar por documento (parcial)
GET /api/orders/search?documentNumber=1234567890

# Buscar por teléfono (parcial)
GET /api/orders/search?phoneNumber=3001234567

# Buscar combinando múltiples criterios (AND)
GET /api/orders/search?clientName=Juan&email=cliente@example.com

# Buscar por nombre y documento
GET /api/orders/search?clientName=Juan&documentNumber=1234567890

# Buscar por email y teléfono
GET /api/orders/search?email=cliente@example.com&phoneNumber=3001234567
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "orderNumber": "ORD-20241115143022-456",
    "userId": "08c0a3f9-48d7-40f1-bc86-1e0e88755a55",
    "userName": "Juan Pérez",
    "userEmail": "cliente@example.com",
    ...
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "orderNumber": "ORD-20241115143023-457",
    "userId": "08c0a3f9-48d7-40f1-bc86-1e0e88755a55",
    "userName": "Juan Carlos",
    "userEmail": "cliente@example.com",
    ...
  }
]
```

**Ejemplos de Uso:**

**Caso 1: Cliente busca sus órdenes por email**
```http
GET /api/orders/search?email=cliente@example.com
```

**Caso 2: Administrador busca órdenes por documento**
```http
GET /api/orders/search?documentNumber=1234567890
```

**Caso 3: Buscar orden específica por número**
```http
GET /api/orders/search?orderNumber=ORD-20241115143022-456
```

**Caso 4: Buscar por nombre parcial del cliente**
```http
GET /api/orders/search?clientName=Juan
```
Retornará todas las órdenes de clientes cuyo nombre contenga "Juan" (ej: "Juan Pérez", "Juan Carlos", "María Juan", etc.)

**Caso 5: Búsqueda combinada**
```http
GET /api/orders/search?clientName=Juan&documentNumber=1234
```
Retornará órdenes de clientes cuyo nombre contenga "Juan" Y cuyo documento contenga "1234"

**Códigos de Respuesta:**
- `200 OK`: Órdenes encontradas exitosamente (puede retornar array vacío si no hay coincidencias)
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Si no se proporciona ningún parámetro, se retornan todas las órdenes
- Los resultados están ordenados por fecha de creación descendente (más recientes primero)
- Las búsquedas de texto son parciales: no es necesario el texto completo
- Las búsquedas de nombre y email son case-insensitive (no distinguen mayúsculas/minúsculas)
- Los múltiples criterios se combinan con AND: todos deben cumplirse

---

### 9. Obtener Órdenes por Rango de Fechas

**Endpoint:** `GET /orders/date-range`

**Descripción:** Obtiene todas las órdenes creadas dentro de un rango de fechas específico. Incluye las fechas de inicio y fin (inclusive).

**Parámetros de Query:**
- `startDate` (String, requerido): Fecha de inicio en formato ISO 8601 (ej: `2024-11-01T00:00:00`)
- `endDate` (String, requerido): Fecha de fin en formato ISO 8601 (ej: `2024-11-30T23:59:59`)

**Formato de Fecha:**
- Formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`
- Ejemplo: `2024-11-01T00:00:00` (1 de noviembre de 2024 a las 00:00:00)
- Ejemplo: `2024-11-30T23:59:59` (30 de noviembre de 2024 a las 23:59:59)

**Validaciones:**
- La fecha de inicio debe ser anterior o igual a la fecha de fin
- Si la fecha de inicio es posterior a la fecha de fin, retorna `400 Bad Request`

**Ejemplos de Request:**

```http
# Órdenes del mes de noviembre 2024
GET /api/orders/date-range?startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59

# Órdenes de un día específico
GET /api/orders/date-range?startDate=2024-11-15T00:00:00&endDate=2024-11-15T23:59:59

# Órdenes de la última semana
GET /api/orders/date-range?startDate=2024-11-08T00:00:00&endDate=2024-11-15T23:59:59

# Órdenes del año 2024
GET /api/orders/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
```

**cURL Ejemplo:**
```bash
curl "http://localhost:8080/api/orders/date-range?startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59"
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "orderNumber": "ORD-20241115143022-456",
    "createdAt": "2024-11-15T14:30:22",
    ...
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "orderNumber": "ORD-20241116143023-457",
    "createdAt": "2024-11-16T14:30:23",
    ...
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Órdenes obtenidas exitosamente (puede retornar array vacío si no hay órdenes en el rango)
- `400 Bad Request`: Fechas inválidas, formato incorrecto, o fecha de inicio posterior a fecha de fin
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Las fechas deben estar en formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`
- El rango es inclusivo: incluye órdenes creadas exactamente en `startDate` y `endDate`
- Los resultados están ordenados por fecha de creación descendente (más recientes primero)
- Para buscar órdenes de un día completo, usar `00:00:00` como hora de inicio y `23:59:59` como hora de fin

**Ejemplos de Uso:**

**Caso 1: Reporte mensual**
```http
GET /api/orders/date-range?startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59
```

**Caso 2: Órdenes de hoy**
```http
GET /api/orders/date-range?startDate=2024-11-15T00:00:00&endDate=2024-11-15T23:59:59
```

**Caso 3: Órdenes de la última semana**
```http
GET /api/orders/date-range?startDate=2024-11-08T00:00:00&endDate=2024-11-15T23:59:59
```

---

## ✏️ ENDPOINTS PATCH

### 1. Actualizar Estado de Orden

**Endpoint:** `PATCH /orders/{id}/status`

**Descripción:** Actualiza el estado de una orden existente.

**Parámetros:**
- `id` (path, UUID, requerido): ID único de la orden
- `status` (query, String, requerido): Nuevo estado de la orden

**Valores Válidos para `status`:**
- `PENDING`
- `CONFIRMED`
- `PROCESSING`
- `SHIPPED`
- `DELIVERED`
- `CANCELLED`

**Ejemplo de Request:**
```http
PATCH /api/orders/123e4567-e89b-12d3-a456-426614174000/status?status=CONFIRMED
```

**cURL Ejemplo:**
```bash
curl -X PATCH "http://localhost:8080/api/orders/123e4567-e89b-12d3-a456-426614174000/status?status=CONFIRMED"
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orderNumber": "ORD-20241115143022-456",
  "status": "CONFIRMED",
  "updatedAt": "2024-11-15T15:45:30",
  ...
}
```

**Códigos de Respuesta:**
- `200 OK`: Estado actualizado exitosamente
- `400 Bad Request`: Datos de entrada inválidos o orden no encontrada
- `404 Not Found`: Orden no encontrada (si el ID no existe)
- `500 Internal Server Error`: Error interno del servidor

**Notas:**
- El campo `updatedAt` se actualiza automáticamente
- Solo se actualiza el campo `status`, los demás campos permanecen iguales

---

### 2. Actualizar Estado de Pago de Orden

**Endpoint:** `PATCH /orders/{id}/payment-status`

**Descripción:** Actualiza el estado de pago de una orden existente.

**Parámetros:**
- `id` (path, UUID, requerido): ID único de la orden
- `paymentStatus` (query, String, requerido): Nuevo estado de pago

**Valores Válidos para `paymentStatus`:**
- `PENDING`
- `PAID`
- `FAILED`
- `REFUNDED`

**Ejemplo de Request:**
```http
PATCH /api/orders/123e4567-e89b-12d3-a456-426614174000/payment-status?paymentStatus=PAID
```

**cURL Ejemplo:**
```bash
curl -X PATCH "http://localhost:8080/api/orders/123e4567-e89b-12d3-a456-426614174000/payment-status?paymentStatus=PAID"
```

**Respuesta Exitosa (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "orderNumber": "ORD-20241115143022-456",
  "paymentStatus": "PAID",
  "updatedAt": "2024-11-15T15:45:30",
  ...
}
```

**Códigos de Respuesta:**
- `200 OK`: Estado de pago actualizado exitosamente
- `400 Bad Request`: Datos de entrada inválidos o orden no encontrada
- `404 Not Found`: Orden no encontrada (si el ID no existe)
- `500 Internal Server Error`: Error interno del servidor

**Notas:**
- El campo `updatedAt` se actualiza automáticamente
- Solo se actualiza el campo `paymentStatus`, los demás campos permanecen iguales

---

## 🔄 Flujos de Trabajo Comunes

### Flujo 1: Consultar Órdenes de un Cliente
```
1. GET /orders/user/{userId} - Obtener todas las órdenes del cliente
2. Para cada orden, usar GET /orders/{id} si se necesita más detalle
```

### Flujo 2: Gestionar Estado de Orden
```
1. GET /orders/status/PENDING - Ver órdenes pendientes
2. PATCH /orders/{id}/status?status=CONFIRMED - Confirmar orden
3. PATCH /orders/{id}/status?status=PROCESSING - Marcar como en proceso
4. PATCH /orders/{id}/status?status=SHIPPED - Marcar como enviada
5. PATCH /orders/{id}/status?status=DELIVERED - Marcar como entregada
```

### Flujo 3: Gestionar Pagos
```
1. GET /orders/payment-status/PENDING - Ver órdenes con pago pendiente
2. PATCH /orders/{id}/payment-status?paymentStatus=PAID - Marcar como pagado
3. Si falla: PATCH /orders/{id}/payment-status?paymentStatus=FAILED
4. Si reembolso: PATCH /orders/{id}/payment-status?paymentStatus=REFUNDED
```

### Flujo 4: Dashboard con Paginación
```
1. GET /orders?page=0&size=20&sort=createdAt,desc - Primera página
2. GET /orders?page=1&size=20&sort=createdAt,desc - Segunda página
3. Usar totalPages del response para navegar
```

### Flujo 5: Búsqueda de Órdenes
```
1. GET /orders/search?email=cliente@example.com - Buscar por email
2. GET /orders/search?documentNumber=1234567890 - Buscar por documento
3. GET /orders/search?clientName=Juan&documentNumber=1234 - Búsqueda combinada
4. GET /orders/search?orderNumber=ORD-2024 - Buscar por número de orden
```

### Flujo 6: Reportes por Fechas
```
1. GET /orders/date-range?startDate=2024-11-01T00:00:00&endDate=2024-11-30T23:59:59 - Reporte mensual
2. GET /orders/date-range?startDate=2024-11-15T00:00:00&endDate=2024-11-15T23:59:59 - Órdenes de hoy
3. Combinar con otros filtros si es necesario
```

---

## ⚠️ Manejo de Errores

### Errores Comunes

**400 Bad Request:**
- Estado o estado de pago inválido
- Parámetros de query mal formateados

**404 Not Found:**
- Orden no encontrada por ID
- Orden no encontrada por número de orden

**500 Internal Server Error:**
- Error en la base de datos
- Error interno del servidor

### Ejemplo de Respuesta de Error
```json
{
  "timestamp": "2024-11-15T15:45:30",
  "status": 404,
  "error": "Not Found",
  "message": "Orden no encontrada",
  "path": "/api/orders/123e4567-e89b-12d3-a456-426614174000"
}
```

---

## 📝 Notas Importantes

1. **UUIDs**: Todos los IDs son UUIDs en formato estándar (ej: `123e4567-e89b-12d3-a456-426614174000`)

2. **Fechas**: Todas las fechas están en formato ISO 8601 (ej: `2024-11-15T14:30:22`)

3. **Decimales**: Todos los valores monetarios son `BigDecimal` con 2 decimales (ej: `1700000.00`)

4. **Paginación**: 
   - La paginación inicia en 0
   - El tamaño por defecto es 20
   - El ordenamiento por defecto es `createdAt,desc`

5. **Ordenamiento**:
   - Formato: `campo,direccion` (ej: `createdAt,desc`)
   - Solo se aplica el primer campo de ordenamiento si se proporcionan múltiples
   - Si el sort es inválido, se usa el sort por defecto

6. **Campos Enriquecidos**:
   - `userName` y `userEmail` se agregan automáticamente en las respuestas
   - `orderItems` incluye información adicional como `variantSku` y `productName`

7. **Actualizaciones**:
   - Los endpoints PATCH solo actualizan el campo especificado
   - El campo `updatedAt` se actualiza automáticamente
   - No se pueden actualizar otros campos mediante estos endpoints

---

## 🔗 Endpoints Relacionados

- **POST /orders**: Crear nueva orden (ya documentado)
- **Usuarios**: `/api/users` - Para obtener información de usuarios
- **Productos**: `/api/products` - Para obtener información de productos y variantes

---

## 📚 Ejemplos de Integración

### JavaScript/TypeScript (Fetch API)
```typescript
// Obtener orden por ID
async function getOrderById(orderId: string) {
  const response = await fetch(`http://localhost:8080/api/orders/${orderId}`);
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Actualizar estado de orden
async function updateOrderStatus(orderId: string, status: string) {
  const response = await fetch(
    `http://localhost:8080/api/orders/${orderId}/status?status=${status}`,
    { method: 'PATCH' }
  );
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Obtener órdenes con paginación
async function getOrders(page: number = 0, size: number = 20, sort: string = 'createdAt,desc') {
  const response = await fetch(
    `http://localhost:8080/api/orders?page=${page}&size=${size}&sort=${sort}`
  );
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Buscar órdenes por múltiples criterios
async function searchOrders(filters: {
  orderId?: string;
  orderNumber?: string;
  clientName?: string;
  email?: string;
  documentNumber?: string;
  phoneNumber?: string;
}) {
  const params = new URLSearchParams();
  if (filters.orderId) params.append('orderId', filters.orderId);
  if (filters.orderNumber) params.append('orderNumber', filters.orderNumber);
  if (filters.clientName) params.append('clientName', filters.clientName);
  if (filters.email) params.append('email', filters.email);
  if (filters.documentNumber) params.append('documentNumber', filters.documentNumber);
  if (filters.phoneNumber) params.append('phoneNumber', filters.phoneNumber);
  
  const response = await fetch(
    `http://localhost:8080/api/orders/search?${params.toString()}`
  );
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Obtener órdenes por rango de fechas
async function getOrdersByDateRange(startDate: string, endDate: string) {
  const response = await fetch(
    `http://localhost:8080/api/orders/date-range?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`
  );
  if (response.ok) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}
```

### Axios
```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Obtener orden por ID
const getOrderById = async (id: string) => {
  const response = await api.get(`/orders/${id}`);
  return response.data;
};

// Actualizar estado de orden
const updateOrderStatus = async (id: string, status: string) => {
  const response = await api.patch(`/orders/${id}/status`, null, {
    params: { status }
  });
  return response.data;
};

// Obtener órdenes con paginación
const getOrders = async (page: number = 0, size: number = 20, sort: string = 'createdAt,desc') => {
  const response = await api.get('/orders', {
    params: { page, size, sort }
  });
  return response.data;
};

// Buscar órdenes por múltiples criterios
const searchOrders = async (filters: {
  orderId?: string;
  orderNumber?: string;
  clientName?: string;
  email?: string;
  documentNumber?: string;
  phoneNumber?: string;
}) => {
  const response = await api.get('/orders/search', {
    params: filters
  });
  return response.data;
};

// Obtener órdenes por rango de fechas
const getOrdersByDateRange = async (startDate: string, endDate: string) => {
  const response = await api.get('/orders/date-range', {
    params: { startDate, endDate }
  });
  return response.data;
};
```

---

**Última actualización:** 2024-11-15
**Versión API:** 2.0.0

