# 🛠️ Sistema de Gestión - Ferretería Cruz

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![HTML/CSS/JS](https://img.shields.io/badge/Frontend-E34F26?style=for-the-badge&logo=html5&logoColor=white)

Un sistema web robusto y escalable diseñado para la gestión integral de inventarios, punto de venta (POS) y control de flujo de productos (Kardex). Desarrollado con **Java EE (Servlets & JSP)** utilizando arquitectura **MVC** y aplicando **Patrones de Diseño** para un código limpio y mantenible.

Ideal como proyecto de portafolio o como prototipo funcional para pequeños y medianos negocios del sector ferretero o tecnológico.

---

## ✨ Características Principales

* 📦 **Gestión de Inventario:** CRUD completo de productos, control de stock mínimo y máximo, y gestión de números de serie (S/N) para hardware/herramientas.
* 📊 **Kardex y Mermas:** Registro histórico de movimientos de entrada y salida. Control de productos dañados o perdidos (Mermas).
* 🛒 **Punto de Venta (POS):** Interfaz ágil para registrar ventas con diferentes métodos de pago.
* 🧾 **Emisión de Comprobantes:** Generación de Boletas y Facturas.
* 🔔 **Notificaciones en Tiempo Real:** Alertas visuales cuando un producto alcanza su stock mínimo.
* 👥 **Gestión de Usuarios y Roles:** Control de acceso basado en roles (Administrador, Almacén, Ventas).

---

## 🏗️ Arquitectura y Patrones de Diseño

Este proyecto fue estructurado aplicando buenas prácticas de ingeniería de software:

* **Arquitectura MVC:** Separación clara entre la vista (JSP), el controlador (Servlets) y el modelo (Clases Java y DAO).
* **Patrón Data Access Object (DAO):** Abstracción y encapsulamiento de todos los accesos a la base de datos MySQL.
* **Patrones de Diseño (GoF) Implementados:**
    * **Factory Pattern:** Utilizado para la creación de diferentes tipos de comprobantes (`Factura`, `Boleta`).
    * **Strategy Pattern:** Implementado para procesar diferentes métodos de pago de forma dinámica (`PagoEfectivo`, `PagoTarjeta`, `PagoTransferencia`).
    * **Observer Pattern:** Aplicado en el `GestorStock` para notificar automáticamente al área de logística o administración cuando el stock de un producto es crítico.
    * **Adapter Pattern:** Preparado para integrar pasarelas de pago externas.

---

## 💻 Tecnologías Utilizadas

* **Backend:** Java 17+, Servlets, JSP (JavaServer Pages)
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla), AJAX
* **Base de Datos:** MySQL 8.0+
* **Servidor de Aplicaciones:** Apache Tomcat 10.x
* **Conector:** MySQL Connector/J 9.6.0

---

## 🚀 Guía de Instalación

Sigue estos pasos para ejecutar el proyecto en tu entorno local:

### 1. Requisitos Previos
* Tener instalado [Java JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) o superior.
* Tener instalado [Apache Tomcat 10](https://tomcat.apache.org/download-10.cgi).
* Tener [MySQL Server](https://dev.mysql.com/downloads/mysql/) corriendo en el puerto `3306`.
* Un IDE compatible con Java EE (Recomendado: **Apache NetBeans 16+** o Eclipse Enterprise).

### 2. Configuración de la Base de Datos
1.  Abre tu gestor de base de datos preferido (MySQL Workbench, DBeaver, phpMyAdmin).
2.  Ejecuta el script SQL incluido en el proyecto para recrear la base de datos y las tablas:
    ```bash
    Ruta del script: /Base de datos y jar/bd-ferreteriacruz.sql
    ```

### 3. Configuración del Proyecto en el IDE
1.  Clona este repositorio o abre el proyecto `SistemaFerreteriaCruz` en tu IDE.
2.  Añade el driver JDBC al `Libraries` o `Build Path` de tu proyecto:
    ```bash
    Ruta de la librería: /Base de datos y jar/mysql-connector-j-9.6.0.jar
    ```
3.  Verifica las credenciales de la base de datos. Si tu usuario o contraseña de MySQL son diferentes, cámbialos en la clase de configuración:
    ```java
    // Archivo: src/java/ferreteriacruz/config/Conexion.java
    private static final String USUARIO = "root";
    private static final String PASSWORD = "tu_contraseña_aqui";
    ```

### 4. Despliegue
1.  Haz clic derecho sobre el proyecto y selecciona **Clean and Build** (Limpiar y Construir).
2.  Ejecuta el proyecto (**Run**) asegurándote de usar Apache Tomcat 10 como servidor local.
3.  Accede a la aplicación desde tu navegador en: `http://localhost:8080/SistemaFerreteriaCruz`

---

## 🔐 Credenciales de Prueba

Una vez levantado el sistema, puedes ingresar con el siguiente usuario por defecto:

* **Usuario:** `admin`
* **Contraseña:** `123456`
* **Rol:** Administrador General

---

## 🤝 Contribución
Si deseas contribuir a este proyecto, eres libre de hacer un _fork_ del repositorio y enviar tus _Pull Requests_. Las mejoras en la interfaz de usuario, reportes en PDF o refactorización de código siempre son bienvenidas.

## 📄 Licencia
Este proyecto se distribuye bajo la licencia **MIT**. Puedes usarlo, modificarlo y distribuirlo libremente.
