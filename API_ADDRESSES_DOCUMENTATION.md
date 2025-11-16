# 📍 API de Direcciones - Documentación Completa

Esta documentación describe todos los endpoints disponibles para la gestión de direcciones de usuarios en el sistema Surequinos Backend.

**Base URL:** `http://localhost:8080/api`

---

## 📋 Tabla de Contenidos

1. [Modelos de Datos](#modelos-de-datos)
2. [Endpoints GET](#-endpoints-get)
3. [Endpoints POST](#-endpoints-post)
4. [Flujos de Trabajo Comunes](#-flujos-de-trabajo-comunes)
5. [Manejo de Errores](#️-manejo-de-errores)
6. [Ejemplos de Integración](#-ejemplos-de-integración)

---

## 📊 Modelos de Datos

### AddressDto

Representa una dirección completa en el sistema.

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "223e4567-e89b-12d3-a456-426614174001",
  "street": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "additionalInfo": "Apartamento 301, Edificio Los Rosales",
  "isDefault": true,
  "createdAt": "2024-11-16T10:30:00",
  "updatedAt": "2024-11-16T10:30:00"
}
```

**Campos:**
- `id` (UUID): ID único de la dirección
- `userId` (UUID): ID del usuario propietario de la dirección
- `street` (String, requerido): Calle y número
- `city` (String, requerido): Ciudad
- `state` (String, opcional): Estado/Departamento
- `country` (String, opcional): País
- `additionalInfo` (String, opcional): Información adicional (referencias, apartamento, etc.)
- `isDefault` (Boolean): Indica si es la dirección por defecto del usuario
- `createdAt` (DateTime): Fecha de creación
- `updatedAt` (DateTime): Fecha de última actualización

### CreateAddressRequest

Request DTO para crear una nueva dirección.

```json
{
  "street": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "additionalInfo": "Apartamento 301, Edificio Los Rosales",
  "isDefault": false
}
```

**Campos:**
- `street` (String, requerido): Calle y número
- `city` (String, requerido): Ciudad
- `state` (String, opcional): Estado/Departamento
- `country` (String, opcional): País
- `additionalInfo` (String, opcional): Información adicional (referencias, apartamento, etc.)
- `isDefault` (Boolean, opcional): Indica si es la dirección por defecto. Si es la primera dirección del usuario, se marca automáticamente como por defecto.

**Validaciones:**
- `street`: No puede estar vacío
- `city`: No puede estar vacío

---

## 🔍 ENDPOINTS GET

### 1. Obtener Direcciones por Email de Usuario

**Endpoint:** `GET /addresses/by-email/{email}`

**Descripción:** Retorna todas las direcciones asociadas a un usuario buscándolo por su correo electrónico. Las direcciones se ordenan con la dirección por defecto primero, luego por fecha de creación descendente (más recientes primero).

**Parámetros de Path:**
- `email` (String, requerido): Correo electrónico del usuario. Debe estar codificado en la URL (ej: `@` se codifica como `%40`)

**Ejemplo de Request:**
```http
GET /api/addresses/by-email/cliente@example.com
```

**cURL Ejemplo:**
```bash
curl "http://localhost:8080/api/addresses/by-email/cliente@example.com"
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "userId": "223e4567-e89b-12d3-a456-426614174001",
    "street": "Calle 123 #45-67",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "Colombia",
    "additionalInfo": "Apartamento 301, Edificio Los Rosales",
    "isDefault": true,
    "createdAt": "2024-11-16T10:30:00",
    "updatedAt": "2024-11-16T10:30:00"
  },
  {
    "id": "323e4567-e89b-12d3-a456-426614174002",
    "userId": "223e4567-e89b-12d3-a456-426614174001",
    "street": "Carrera 50 #100-20",
    "city": "Medellín",
    "state": "Antioquia",
    "country": "Colombia",
    "additionalInfo": null,
    "isDefault": false,
    "createdAt": "2024-11-15T09:20:00",
    "updatedAt": "2024-11-15T09:20:00"
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Direcciones obtenidas exitosamente (puede retornar array vacío si el usuario no tiene direcciones)
- `404 Not Found`: Usuario no encontrado
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Solo se retornan direcciones de usuarios con status `ACTIVE` o `INACTIVE`
- Los usuarios con status `DELETED` no aparecen en los resultados
- Las direcciones se ordenan con la dirección por defecto primero
- Si el usuario no tiene direcciones, se retorna un array vacío `[]`
- El email debe coincidir exactamente (case-sensitive)
- El email debe estar codificado en la URL si contiene caracteres especiales

---

### 2. Obtener Direcciones por ID de Usuario

**Endpoint:** `GET /addresses/user/id/{userId}`

**Descripción:** Retorna todas las direcciones asociadas a un usuario por su ID. Las direcciones se ordenan con la dirección por defecto primero, luego por fecha de creación descendente.

**Parámetros de Path:**
- `userId` (UUID, requerido): ID único del usuario

**Ejemplo de Request:**
```http
GET /api/addresses/user/id/123e4567-e89b-12d3-a456-426614174000
```

**cURL Ejemplo:**
```bash
curl "http://localhost:8080/api/addresses/user/id/123e4567-e89b-12d3-a456-426614174000"
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "userId": "223e4567-e89b-12d3-a456-426614174001",
    "street": "Calle 123 #45-67",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "Colombia",
    "additionalInfo": "Apartamento 301",
    "isDefault": true,
    "createdAt": "2024-11-16T10:30:00",
    "updatedAt": "2024-11-16T10:30:00"
  }
]
```

**Códigos de Respuesta:**
- `200 OK`: Direcciones obtenidas exitosamente (puede retornar array vacío si el usuario no tiene direcciones)
- `404 Not Found`: Usuario no encontrado
- `500 Internal Server Error`: Error interno del servidor

**Notas Importantes:**
- Las direcciones se ordenan con la dirección por defecto primero
- Si el usuario no tiene direcciones, se retorna un array vacío `[]`

---

## ➕ ENDPOINTS POST

### 1. Crear Nueva Dirección

**Endpoint:** `POST /addresses/user/{userId}`

**Descripción:** Crea una nueva dirección para un usuario. Si se marca como por defecto, se desmarca automáticamente la dirección por defecto anterior. Si es la primera dirección del usuario, se marca automáticamente como por defecto.

**Parámetros de Path:**
- `userId` (UUID, requerido): ID único del usuario

**Body (JSON):**
```json
{
  "street": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "additionalInfo": "Apartamento 301, Edificio Los Rosales",
  "isDefault": false
}
```

**Ejemplo de Request:**
```http
POST /api/addresses/user/123e4567-e89b-12d3-a456-426614174000
Content-Type: application/json

{
  "street": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "additionalInfo": "Apartamento 301, Edificio Los Rosales",
  "isDefault": false
}
```

**cURL Ejemplo:**
```bash
curl -X POST http://localhost:8080/api/addresses/user/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -d '{
    "street": "Calle 123 #45-67",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "Colombia",
    "additionalInfo": "Apartamento 301, Edificio Los Rosales",
    "isDefault": false
  }'
```

**Respuesta Exitosa (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "223e4567-e89b-12d3-a456-426614174001",
  "street": "Calle 123 #45-67",
  "city": "Bogotá",
  "state": "Cundinamarca",
  "country": "Colombia",
  "additionalInfo": "Apartamento 301, Edificio Los Rosales",
  "isDefault": true,
  "createdAt": "2024-11-16T10:30:00",
  "updatedAt": "2024-11-16T10:30:00"
}
```

**Códigos de Respuesta:**
- `201 Created`: Dirección creada exitosamente
- `400 Bad Request`: Datos de entrada inválidos (validaciones fallidas)
- `404 Not Found`: Usuario no encontrado
- `500 Internal Server Error`: Error interno del servidor

**Validaciones:**
- El usuario debe existir (ID válido)
- `street`: No puede estar vacío
- `city`: No puede estar vacío

**Notas Importantes:**
- Si `isDefault` es `true`, se desmarca automáticamente la dirección por defecto anterior del usuario
- Si es la primera dirección del usuario, se marca automáticamente como por defecto (`isDefault = true`), incluso si se envía `false`
- Todos los demás campos son opcionales
- La dirección se asocia automáticamente al usuario especificado

---

## 🔄 Flujos de Trabajo Comunes

### Flujo 1: Crear Dirección y Obtenerla
```
1. POST /addresses/user/{userId} - Crear nueva dirección
2. GET /addresses/user/{email} - Obtener todas las direcciones del usuario
3. Verificar que la dirección aparece en la lista
```

### Flujo 2: Obtener Direcciones de un Usuario
```
1. GET /addresses/user/{email} - Obtener direcciones por email
2. Filtrar la dirección por defecto (isDefault = true)
3. Mostrar todas las direcciones disponibles
```

### Flujo 3: Crear Primera Dirección (Automáticamente por Defecto)
```
1. POST /addresses/user/{userId} - Crear primera dirección (isDefault puede ser false)
2. El sistema automáticamente marca isDefault = true
3. GET /addresses/user/{email} - Verificar que isDefault = true
```

### Flujo 4: Cambiar Dirección por Defecto
```
1. GET /addresses/user/{email} - Obtener direcciones actuales
2. POST /addresses/user/{userId} - Crear nueva dirección con isDefault = true
3. El sistema automáticamente desmarca la dirección por defecto anterior
4. GET /addresses/user/{email} - Verificar que solo la nueva tiene isDefault = true
```

### Flujo 5: Crear Orden con Dirección
```
1. POST /orders - Crear orden incluyendo campo "address" en el request
2. El sistema crea automáticamente la dirección para el usuario
3. GET /addresses/user/{email} - Verificar que la dirección fue creada
```

---

## ⚠️ Manejo de Errores

### Errores Comunes

**400 Bad Request - Datos Inválidos**
```json
{
  "timestamp": "2024-11-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada inválidos"
}
```

**404 Not Found - Usuario No Encontrado**
```json
{
  "timestamp": "2024-11-16T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario no encontrado"
}
```

**500 Internal Server Error**
```json
{
  "timestamp": "2024-11-16T10:30:00",
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

// Obtener direcciones por email de usuario
async function getAddressesByEmail(email: string) {
  const encodedEmail = encodeURIComponent(email);
  const response = await fetch(`${API_BASE_URL}/addresses/by-email/${encodedEmail}`);
  if (response.ok) {
    return await response.json();
  }
  if (response.status === 404) {
    return [];
  }
  throw new Error(`Error: ${response.status}`);
}

// Obtener direcciones por ID de usuario
async function getAddressesByUserId(userId: string) {
  const response = await fetch(`${API_BASE_URL}/addresses/user/id/${userId}`);
  if (response.ok) {
    return await response.json();
  }
  if (response.status === 404) {
    return [];
  }
  throw new Error(`Error: ${response.status}`);
}

// Crear nueva dirección
async function createAddress(userId: string, addressData: {
  street: string;
  city: string;
  state?: string;
  country?: string;
  additionalInfo?: string;
  isDefault?: boolean;
}) {
  const response = await fetch(`${API_BASE_URL}/addresses/user/${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(addressData),
  });
  if (response.ok || response.status === 201) {
    return await response.json();
  }
  throw new Error(`Error: ${response.status}`);
}

// Ejemplos de uso:
// const addresses = await getAddressesByEmail('cliente@example.com');
// const defaultAddress = addresses.find(addr => addr.isDefault);
// const newAddress = await createAddress(userId, { street: 'Calle 123', city: 'Bogotá', isDefault: true });
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

// Obtener direcciones por email de usuario
const getAddressesByEmail = async (email: string) => {
  try {
    const response = await api.get(`/addresses/by-email/${encodeURIComponent(email)}`);
    return response.data;
  } catch (error: any) {
    if (error.response?.status === 404) {
      return [];
    }
    throw error;
  }
};

// Obtener direcciones por ID de usuario
const getAddressesByUserId = async (userId: string) => {
  try {
    const response = await api.get(`/addresses/user/id/${userId}`);
    return response.data;
  } catch (error: any) {
    if (error.response?.status === 404) {
      return [];
    }
    throw error;
  }
};

// Crear nueva dirección
const createAddress = async (userId: string, addressData: {
  street: string;
  city: string;
  state?: string;
  country?: string;
  additionalInfo?: string;
  isDefault?: boolean;
}) => {
  const response = await api.post(`/addresses/user/${userId}`, addressData);
  return response.data;
};

// Ejemplos de uso:
// const addresses = await getAddressesByEmail('cliente@example.com');
// const defaultAddress = addresses.find((addr: any) => addr.isDefault);
// const newAddress = await createAddress(userId, { street: 'Calle 123', city: 'Bogotá', isDefault: true });
```

### React Hook Example

```typescript
import { useState, useEffect } from 'react';

interface Address {
  id: string;
  userId: string;
  street: string;
  city: string;
  state?: string;
  country?: string;
  additionalInfo?: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}

function useAddresses(email: string) {
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAddresses = async () => {
      try {
        setLoading(true);
        const encodedEmail = encodeURIComponent(email);
        const response = await fetch(`http://localhost:8080/api/addresses/by-email/${encodedEmail}`);
        if (response.ok) {
          const data = await response.json();
          setAddresses(data);
        } else {
          setError('Error al cargar direcciones');
        }
      } catch (err) {
        setError('Error de conexión');
      } finally {
        setLoading(false);
      }
    };

    if (email) {
      fetchAddresses();
    }
  }, [email]);

  const createAddress = async (userId: string, addressData: {
    street: string;
    city: string;
    state?: string;
    country?: string;
    additionalInfo?: string;
    isDefault?: boolean;
  }) => {
    try {
      const response = await fetch(`http://localhost:8080/api/addresses/user/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(addressData),
      });
      if (response.ok || response.status === 201) {
        const newAddress = await response.json();
        setAddresses([...addresses, newAddress]);
        return newAddress;
      }
      throw new Error('Error al crear dirección');
    } catch (err) {
      setError('Error al crear dirección');
      throw err;
    }
  };

  return { addresses, loading, error, createAddress };
}

// Uso en componente:
function AddressList({ userEmail }: { userEmail: string }) {
  const { addresses, loading, error, createAddress } = useAddresses(userEmail);
  const defaultAddress = addresses.find(addr => addr.isDefault);

  if (loading) return <div>Cargando direcciones...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h2>Direcciones</h2>
      {defaultAddress && (
        <div>
          <h3>Dirección por Defecto</h3>
          <p>{defaultAddress.street}, {defaultAddress.city}</p>
        </div>
      )}
      <ul>
        {addresses.map(address => (
          <li key={address.id}>
            {address.street}, {address.city} {address.isDefault && '(Por defecto)'}
          </li>
        ))}
      </ul>
    </div>
  );
}
```

---

## 📝 Notas Adicionales

### Comportamiento de Dirección por Defecto

1. **Primera Dirección**: Si un usuario no tiene direcciones y se crea la primera, automáticamente se marca como `isDefault = true`, incluso si en el request se envía `isDefault = false`.

2. **Cambiar Dirección por Defecto**: Si se crea una nueva dirección con `isDefault = true`, el sistema automáticamente desmarca la dirección por defecto anterior (si existe).

3. **Orden de Resultados**: Las direcciones siempre se ordenan con la dirección por defecto primero, luego por fecha de creación descendente.

### Integración con Órdenes

Cuando se crea una orden (`POST /orders`), hay tres formas de manejar la dirección:

#### 1. Usar Dirección Existente (Recomendado)
Si el usuario ya tiene direcciones guardadas, se puede enviar el `addressId` de una dirección existente. **NO se creará una nueva dirección**, solo se validará que la dirección pertenece al usuario.

```json
{
  "email": "cliente@example.com",
  "documentNumber": "1234567890",
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "addressId": "123e4567-e89b-12d3-a456-426614174000",
  "items": [...],
  ...
}
```

**Ventajas:**
- No duplica direcciones
- Más eficiente
- Usa direcciones ya validadas del usuario

#### 2. Crear Nueva Dirección
Si se quiere guardar una nueva dirección en el perfil del usuario, se envía el objeto `address`. **Se creará una nueva dirección** asociada al usuario.

```json
{
  "email": "cliente@example.com",
  "documentNumber": "1234567890",
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "address": {
    "street": "Calle 123 #45-67",
    "city": "Bogotá",
    "state": "Cundinamarca",
    "country": "Colombia",
    "additionalInfo": "Apartamento 301",
    "isDefault": false
  },
  "items": [...],
  ...
}
```

**Ventajas:**
- Guarda la dirección para futuras órdenes
- Útil cuando es la primera vez que el usuario compra

#### 3. Solo Usar Dirección como Texto
Si no se envía ni `addressId` ni `address`, solo se usa el campo `shippingAddress` como texto en la orden. **NO se guarda ninguna dirección** en el perfil del usuario.

```json
{
  "email": "cliente@example.com",
  "documentNumber": "1234567890",
  "shippingAddress": "Calle 123 #45-67, Bogotá",
  "items": [...],
  ...
}
```

**Ventajas:**
- No guarda direcciones innecesarias
- Útil para órdenes únicas o direcciones temporales

#### Prioridad y Validaciones

1. **Prioridad**: Si se envía `addressId`, tiene prioridad sobre `address`. El campo `address` se ignora si `addressId` está presente.

2. **Validación**: Si se envía `addressId`, el sistema valida que:
   - La dirección existe
   - La dirección pertenece al usuario (por email y documento)
   - Si la validación falla, se retorna error `400 Bad Request`

3. **Comportamiento**:
   - `addressId` presente → Usa dirección existente, NO crea nueva
   - `address` presente (sin `addressId`) → Crea nueva dirección
   - Ninguno presente → Solo usa `shippingAddress` como texto

**Ejemplo de flujo completo:**
```typescript
// 1. Obtener direcciones del usuario
const addresses = await getAddressesByEmail('cliente@example.com');

// 2. Usuario selecciona una dirección existente
const selectedAddress = addresses[0];

// 3. Crear orden usando la dirección existente
const order = await createOrder({
  email: 'cliente@example.com',
  documentNumber: '1234567890',
  shippingAddress: `${selectedAddress.street}, ${selectedAddress.city}`,
  addressId: selectedAddress.id, // Usa dirección existente
  items: [...]
});

// O si el usuario quiere agregar una nueva dirección:
const orderWithNewAddress = await createOrder({
  email: 'cliente@example.com',
  documentNumber: '1234567890',
  shippingAddress: 'Calle Nueva 456',
  address: { // Crea nueva dirección
    street: 'Calle Nueva 456',
    city: 'Bogotá',
    isDefault: false
  },
  items: [...]
});
```

---

**Última actualización:** 2024-11-16

