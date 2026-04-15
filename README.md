# Surequinos Backend - Ecommerce API

API REST profesional para el ecommerce de productos de talabartería Surequinos, desarrollada con Spring Boot 3.5.7 y Java 21.

## 🚀 Características

- **Arquitectura Hexagonal** con separación clara de responsabilidades
- **Queries Nativas** optimizadas para PostgreSQL
- **Documentación Swagger** completa y interactiva
- **DTOs y Mappers** con MapStruct para transferencia de datos
- **Gestión completa de productos con variantes** (color, talla, tipo)
- **Sistema de categorías jerárquicas**
- **Control de stock en tiempo real**
- **Gestión de imágenes con Cloudflare R2** - Subida, almacenamiento y renderizado dinámico
- **Imágenes específicas por variante** - Cada variante puede tener su propia imagen
- **Manejo de excepciones centralizado**
- **Configuración CORS** para frontend
- **Logging estructurado**

## 🏗️ Arquitectura

```
src/main/java/com/surequinos/surequinos_backend/
├── application/
│   ├── dto/                    # DTOs para transferencia de datos
│   ├── mapper/                 # Mappers con MapStruct
│   └── service/                # Lógica de negocio
├── domain/
│   └── entity/                 # Entidades JPA
├── infrastructure/
│   ├── config/                 # Configuraciones
│   ├── repository/             # Repositorios con queries nativas
│   └── web/
│       ├── controller/         # Controladores REST
│       └── exception/          # Manejo de excepciones
└── SurequinosBackendApplication.java
```

## 🛠️ Tecnologías

- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **PostgreSQL**
- **MapStruct** para mapeo de objetos
- **Swagger/OpenAPI 3** para documentación
- **Lombok** para reducir boilerplate
- **Maven** para gestión de dependencias

## 📋 Prerrequisitos

- Java 21 o superior
- Maven 3.8+
- PostgreSQL 12+
- IDE con soporte para Java (IntelliJ IDEA, Eclipse, VS Code)

## ⚙️ Configuración

### 1. Base de Datos

Crear base de datos PostgreSQL:

```sql
CREATE DATABASE surequinos_db;
CREATE USER surequinos_user WITH PASSWORD 'surequinos_pass';
GRANT ALL PRIVILEGES ON DATABASE surequinos_db TO surequinos_user;
```

### 2. Configuración de Aplicación

Editar `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/surequinos_db
spring.datasource.username=surequinos_user
spring.datasource.password=surequinos_pass

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop  # Para desarrollo
# spring.jpa.hibernate.ddl-auto=validate   # Para producción

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Cloudflare R2 (prefijo surequinos.r2 — evita error Railpack "secret cloudflare: not found" en Railway)
surequinos.r2.account-id=your-account-id
surequinos.r2.access-key-id=your-access-key-id
surequinos.r2.secret-access-key=your-secret-access-key
surequinos.r2.bucket-name=surequinos-images
surequinos.r2.region=auto
surequinos.r2.endpoint=https://your-account-id.r2.cloudflarestorage.com
surequinos.r2.public-url=https://your-custom-domain.com
```

### 3. Configuración de Cloudflare R2

1. **Crear cuenta en Cloudflare** y habilitar R2 Object Storage
2. **Crear bucket** llamado `surequinos-images`
3. **Generar API Token** con permisos de lectura/escritura
4. **Configurar dominio personalizado** para acceso público a imágenes
5. **Actualizar credenciales** en `application-local.properties` (o variables `SUREQUINOS_R2_*` / `surequinos.r2.*` en Railway)

### 3. Ejecutar la Aplicación

```bash
# Clonar el repositorio
git clone <repository-url>
cd surequinos-backend

# Compilar y ejecutar
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api`

## 📚 Documentación API

### Swagger UI
Acceder a la documentación interactiva en:
`http://localhost:8080/api/swagger-ui.html`

### Endpoints Principales

#### Categorías
- `GET /api/categories` - Obtener todas las categorías
- `GET /api/categories/with-product-count` - Categorías con conteo
- `GET /api/categories/slug/{slug}` - Buscar por slug
- `POST /api/categories` - Crear categoría
- `PUT /api/categories/{id}` - Actualizar categoría
- `DELETE /api/categories/{id}` - Eliminar categoría

#### Productos
- `GET /api/products` - Obtener productos con paginación
- `GET /api/products/full` - Vista completa con variantes
- `GET /api/products/slug/{slug}` - Buscar por slug
- `GET /api/products/search?q={texto}` - Búsqueda por texto
- `GET /api/products/category/{categoryId}` - Por categoría
- `POST /api/products` - Crear producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

#### Variantes
- `GET /api/variants/product/{productId}` - Variantes de un producto
- `GET /api/variants/sku/{sku}` - Buscar por SKU
- `GET /api/variants/product/{productId}/available` - Solo disponibles
- `POST /api/variants` - Crear variante
- `PUT /api/variants/{id}` - Actualizar variante
- `PATCH /api/variants/{id}/stock` - Actualizar stock
- `DELETE /api/variants/{id}` - Eliminar variante

#### Tienda (Frontend Optimizado)
- `GET /api/shop/products` - Productos con filtros
- `GET /api/shop/categories` - Categorías para navegación
- `GET /api/shop/filters` - Filtros disponibles
- `GET /api/shop/products/{slug}` - Detalle de producto
- `GET /api/shop/products/{productId}/variant-image` - Imagen específica de variante

#### Imágenes (Cloudflare R2)
- `POST /api/images/product/{productId}` - Subir imagen de producto
- `POST /api/images/product/{productId}/multiple` - Subir múltiples imágenes
- `POST /api/images/variant/{productId}/{variantSku}` - Subir imagen de variante
- `DELETE /api/images?imageUrl={url}` - Eliminar imagen
- `GET /api/images/exists?imageUrl={url}` - Verificar existencia

#### Productos y Variantes con Imágenes
- `POST /api/products/with-images` - Crear producto con imágenes
- `POST /api/variants/with-image` - Crear variante con imagen

## 🗄️ Modelo de Datos

### Entidades Principales

#### Category
- Soporte para categorías jerárquicas
- Slug único para URLs amigables
- Orden de visualización configurable

#### Product
- Información base del producto
- Array de imágenes
- Relación con categoría
- Precio base de referencia

#### Variant
- Variantes específicas con SKU único
- Atributos: color, talla, tipo
- Precio y stock individuales
- Control de disponibilidad

#### AttributeOption
- Opciones predefinidas para atributos
- Colores, tallas, tipos disponibles
- Orden de visualización

### Vista Optimizada

**v_products_full**: Vista que combina productos con sus variantes en formato JSON para consultas optimizadas del frontend.

## 🔧 Desarrollo

### Agregar Nueva Funcionalidad

1. **Crear entidad** en `domain/entity/`
2. **Crear repositorio** con queries nativas en `infrastructure/repository/`
3. **Crear DTOs** en `application/dto/`
4. **Crear mapper** en `application/mapper/`
5. **Implementar servicio** en `application/service/`
6. **Crear controlador** en `infrastructure/web/controller/`
7. **Documentar con Swagger** usando anotaciones

### Queries Nativas

Todas las consultas complejas utilizan queries nativas de PostgreSQL para máximo rendimiento:

```java
@Query(value = """
    SELECT p.*, c.name as category_name 
    FROM products p 
    LEFT JOIN categories c ON p.category_id = c.id 
    WHERE p.is_active = true
    """, nativeQuery = true)
List<Product> findActiveProductsWithCategory();
```

### Manejo de Errores

Sistema centralizado de manejo de excepciones con respuestas consistentes:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Los datos enviados no son válidos",
  "details": {
    "name": "El nombre es obligatorio",
    "slug": "El slug debe ser único"
  }
}
```

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

## 📦 Despliegue

### Desarrollo
```bash
mvn spring-boot:run
```

### Producción
```bash
# Generar JAR
mvn clean package -DskipTests

# Ejecutar JAR
java -jar target/surequinos-backend-0.0.1-SNAPSHOT.jar
```

### Docker (Opcional)
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/surequinos-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🔒 Seguridad

- Validación de entrada con Bean Validation
- Sanitización de queries SQL
- CORS configurado para dominios específicos
- Logs de seguridad para auditoría

## 📈 Monitoreo

- Logs estructurados con SLF4J
- Métricas de rendimiento
- Health checks automáticos
- Documentación de API actualizada

## 🤝 Contribución

1. Fork del proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

## 📞 Soporte

Para soporte técnico o consultas:
- Email: contacto@surequinos.com
- Documentación: `http://localhost:8080/api/swagger-ui.html`

---

**Desarrollado con ❤️ para Surequinos Talabartería**