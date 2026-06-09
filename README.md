# 🛠️ Sistema de Gestión - Ferretería Cruz (Sercoplus)

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

## 📸 Capturas de Pantalla

*(Nota: Añade capturas de tu sistema aquí para que el repositorio luzca más atractivo)*
## PARA ESTOS EJEMPLOS DE LAS VISTAS SE UTILIZO PRODUCTOS GAMING/TECNOLOGIA COMO PRUEBA SOLAMENTE.
* *Login*
* <img width="705" height="881" alt="image" src="https://github.com/user-attachments/assets/3b2debe4-53b1-48e2-95fd-c149bf7037a4" />

* *Dashboard / Inventario*
* <img width="934" height="738" alt="image" src="https://github.com/user-attachments/assets/8ce95075-686f-40a0-ba5d-da3b39651e22" />
* <img width="955" height="697" alt="image" src="https://github.com/user-attachments/assets/b1946eda-04c9-4f39-ac91-c878cf41cb90" />

* *Punto de Venta*
* <img width="890" height="919" alt="image" src="https://github.com/user-attachments/assets/2786be19-3c7f-4e37-a1b9-a8a844a24468" />

* *Gestion de Mermas*
* <img width="943" height="602" alt="image" src="https://github.com/user-attachments/assets/7283ac19-3a31-4147-97bf-7124a85b9cbf" />

* *Auditoría de Kardex Valorizado*
* <img width="959" height="560" alt="image" src="https://github.com/user-attachments/assets/5e97c04e-0d20-497c-bdb7-afebd5f68631" />

* *Módulo de Seguridad y Gestión de Personal*
* <img width="1050" height="553" alt="image" src="https://github.com/user-attachments/assets/1089122f-ce75-45b2-9888-4c38c9532506" />

* *Formulario de Registro y Mantenimiento de Hardware*
* <img width="885" height="975" alt="image" src="https://github.com/user-attachments/assets/a6a85265-5634-4ce5-ac1b-3cc408fee71d" />

* *Historial Transaccional y Anulaciones*
* <img width="963" height="542" alt="image" src="https://github.com/user-attachments/assets/84651c73-1aa5-4a5f-8f4b-8450c26d0915" />

* *Historial Transaccional y Anulaciones*
* <img width="1050" height="945" alt="image" src="https://github.com/user-attachments/assets/9fc63b0c-d095-442d-b222-fcf7cf82a2f1" />

 
---

## 🤝 Contribución
Si deseas contribuir a este proyecto, eres libre de hacer un _fork_ del repositorio y enviar tus _Pull Requests_. Las mejoras en la interfaz de usuario, reportes en PDF o refactorización de código siempre son bienvenidas.

## 📄 Licencia
Este proyecto se distribuye bajo la licencia **MIT**. Puedes usarlo, modificarlo y distribuirlo libremente.
