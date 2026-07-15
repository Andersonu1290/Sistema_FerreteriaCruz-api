# 🛠️ Sistema de Gestión API REST - Ferretería Cruz (Backend)

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT_Security-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black)

Una API REST robusta, segura y escalable diseñada para la gestión integral de inventarios, punto de venta (POS) y control de flujo de productos (Kardex). Este proyecto representa el **núcleo (Backend)** del ecosistema de la Ferretería Cruz, construido bajo una arquitectura desacoplada y diseñado para ser consumido por un frontend SPA (Single Page Application) en Vue.js.

Desarrollado con **Spring Boot**, aplicando **Patrones de Diseño (GoF)**, el **Patrón DAO** y múltiples librerías de nivel empresarial para garantizar un código limpio, auditable y de alto rendimiento.

---

## ✨ Características Principales

* 🔒 **Seguridad Stateless (JWT):** Autenticación y autorización mediante JSON Web Tokens. Rutas protegidas según el rol del usuario (Administrador, Almacén, Ventas).
* 📦 **Gestión de Inventario API:** Endpoints para el CRUD completo de productos, control de stock y gestión de números de serie (S/N).
* 📊 **Motor de Reportes Excel:** Generación dinámica de reportes gerenciales descargables (`.xlsx`) analizando grandes volúmenes de datos en memoria.
* 🛒 **Procesamiento de POS y E-commerce:** Endpoints asíncronos para registrar ventas con diferentes métodos de pago y manejar carritos de compras.
* 🧾 **Kardex y Mermas:** Registro transaccional de movimientos de entrada/salida y auditoría de productos dañados o perdidos.
* 📚 **Documentación Interactiva:** Integración nativa con Swagger/OpenAPI para la prueba de endpoints en tiempo real.

---

## 🏗️ Arquitectura y Patrones de Diseño

Este proyecto fue estructurado aplicando estrictas normas de ingeniería de software:

* **Arquitectura Desacoplada (Cliente-Servidor):** El backend expone servicios web en formato JSON, totalmente independiente de la interfaz gráfica.
* **Patrón DTO (Data Transfer Object):** Utilizado para transferir únicamente los datos necesarios entre el cliente y el servidor, ocultando la estructura interna de la base de datos.
* **Patrón DAO (Data Access Object):** Implementado modernamente a través de `Spring Data JPA` para la persistencia segura en MySQL.
* **Patrones de Diseño (GoF) Implementados:**
  * **Factory Pattern:** Creación dinámica de diferentes tipos de comprobantes (`Factura`, `Boleta`).
  * **Strategy Pattern:** Procesamiento de múltiples métodos de pago (`PagoEfectivo`, `PagoTarjeta`, `PagoTransferencia`).
  * **Observer Pattern:** Implementado en el `GestorStock` para disparar eventos asíncronos cuando el inventario alcanza niveles críticos.
  * **Adapter Pattern:** Estructura preparada (`PasarelaExterna`) para integrar pasarelas de pago digitales a futuro.

---

## 💻 Tecnologías y Librerías Utilizadas

* **Framework Core:** Java 17+, Spring Boot 3.x, Spring Web, Spring Data JPA, Spring Security.
* **Base de Datos:** MySQL 8.0+
* **Servidor:** Apache Tomcat (Embebido en Spring Boot).
* **Librerías Empresariales (Core de Negocio):**
  * **Apache Commons (Lang3):** Validación defensiva y sanitización de datos (ej. cadenas nulas o vacías en las transacciones).
  * **Google Guava:** Manejo avanzado de colecciones y estructuras de datos (`Multimap`) para agrupar catálogos de inventario en memoria.
  * **Apache POI:** Generación y exportación dinámica de reportes gerenciales en formato Microsoft Excel.
  * **Logback (SLF4J):** Motor de registro (*logging*) para la auditoría, monitoreo y trazabilidad de eventos y excepciones del servidor.

---

## 🚀 Guía de Instalación y Despliegue

A diferencia de las arquitecturas antiguas, **no necesitas instalar Apache Tomcat externamente**, ya que Spring Boot lo incluye de manera nativa.

### 1. Requisitos Previos
* Tener instalado [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) o superior.
* Tener [MySQL Server](https://dev.mysql.com/downloads/mysql/) corriendo en el puerto local `3306`.
* Un IDE moderno (Recomendado: **IntelliJ IDEA, Eclipse o VS Code con Extension Pack for Java**).

### 2. Configuración de la Base de Datos
1. Abre tu gestor de base de datos preferido (MySQL Workbench, DBeaver).
2. Ejecuta el script SQL incluido en el proyecto para recrear la base de datos y sus datos semilla.

### 3. Configuración del Proyecto en el IDE
1. Clona este repositorio (`Sistema_FerreteriaCruz-api`) y ábrelo en tu IDE.
2. Espera a que el gestor de dependencias (Maven/Gradle) descargue las librerías automáticamente.
3. Verifica las credenciales de la base de datos en el archivo de propiedades:
   ```properties
   # Archivo: src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bd_ferreteria
   spring.datasource.username=root
   spring.datasource.password=tu_contraseña_aqui

```

### 4. Ejecución del Servidor

1. Ejecuta la clase principal del proyecto: `FerreteriacruzBackendApplication.java`.
2. El servidor Tomcat embebido se levantará automáticamente en el puerto `8080`.
3. Podrás verificar que la API está funcionando accediendo a la documentación de **Swagger** desde tu navegador:
```text
http://localhost:8080/swagger-ui/index.html

```



---

## 🔐 Pruebas y Autenticación (Login)

Para consumir la API y obtener un Token JWT, puedes enviar una petición POST al endpoint de Login desde Postman, Swagger o el Frontend con las credenciales por defecto:

* **Endpoint:** `POST /api/v1/auth/login`
* **JSON Request:**
```json
{
  "username": "admin",
  "password": "123456"
}

```


* **Respuesta:** El servidor te devolverá un Token JWT que deberás incluir en los *Headers* (`Authorization: Bearer <token>`) de tus siguientes peticiones.

---

## 🤝 Repositorio del Frontend

Este proyecto está diseñado para funcionar en conjunto con su cliente gráfico web. Puedes encontrar el código fuente de la SPA en Vue.js en su repositorio correspondiente: **[Sistema_FerreteriaCruz]** *(Coloca aquí el enlace a tu repositorio de frontend)*.

## 📄 Licencia

Este proyecto es de carácter académico/profesional y se distribuye bajo la licencia **MIT**.
