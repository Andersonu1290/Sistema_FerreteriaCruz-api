<%-- 
    Document   : ProductoForm
    Created on : 10 may 2026, 12:22:39
    Author     : Anderson
--%>

<%@page import="ferreteriacruz.modelo.Producto"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    Producto p = (Producto) request.getAttribute("producto");
    boolean esEdicion = (p != null);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FerreteriaCruz | Registro de Producto</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=JetBrains+Mono&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="dashboard-container-narrow">
        
        <div class="header-tech">
            <div class="header-title">
                <h2>
                    <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 24px; height: 24px;">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                    </svg>
                    <%= esEdicion ? "Modificar Producto" : "Registrar Nuevo Producto" %>
                </h2>
            </div>
            <a href="ProductoServlet?accion=listar" class="btn-tech">Cancelar y Volver</a>
        </div>

        <div class="form-panel">
            <form action="ProductoServlet?accion=guardar" method="POST" enctype="multipart/form-data">
                
                <input type="hidden" name="txtId" value="<%= esEdicion ? p.getIdProducto() : "" %>">

                <label class="form-label">Código SKU único:</label>
                <input type="text" name="txtSku" class="input-tech input-mono" required placeholder="Ej. Cer" value="<%= esEdicion ? p.getCodigoSKU() : "" %>">

                <label class="form-label">Nombre / Especificación del Producto:</label>
                <input type="text" name="txtNombre" class="input-tech" required placeholder="Ej. Cierra" value="<%= esEdicion ? p.getNombre() : "" %>">
                
                <label class="form-label">Familia / Categoría del Componente:</label>
                <div class="d-flex align-center gap-15 mb-25">
                    <select name="cboCategoria" class="input-tech flex-col" style="margin-bottom: 0;" required>
                        <%@page import="java.util.List"%>
                        <%@page import="ferreteriacruz.modelo.Categoria"%>
                        <% 
                           List<Categoria> cats = (List<Categoria>) request.getAttribute("listaCategorias");
                           if(cats != null) {
                               for(Categoria c : cats) {
                                   String seleccionado = (esEdicion && p.getIdCategoria() == c.getIdCategoria()) ? "selected" : "";
                        %>
                                <option value="<%= c.getIdCategoria() %>" <%= seleccionado %>><%= c.getNombre() %></option>
                        <%     }
                           } 
                        %>
                    </select>
                    <a href="CategoriaServlet?accion=listar" class="btn-action btn-edit" style="padding: 14px 20px; font-size: 14px;">Gestionar Categorías</a>
                </div>

                <div class="flex-row gap-15">
                    <div class="flex-col">
                        <label class="form-label">Stock Inicial/Actual:</label>
                        <input type="number" name="txtStock" class="input-tech input-mono" required min="0" value="<%= esEdicion ? p.getStockActual() : "0" %>">
                    </div>
                    <div class="flex-col">
                        <label class="form-label">Umbral de Alerta Mínimo:</label>
                        <input type="number" name="txtMinimo" class="input-tech input-mono" required min="0" value="<%= esEdicion ? p.getStockMinimo() : "5" %>">
                    </div>
                </div>

                <label class="form-label">Precio Unitario (S/):</label>
                <input type="number" name="txtPrecio" class="input-tech input-mono" step="0.01" required placeholder="Ej. 1500.50" value="<%= esEdicion ? p.getPrecio() : "" %>">

                <label class="form-label mt-15">Fotografía del Componente (Opcional):</label>
                <input type="file" name="txtImagen" class="input-tech" accept="image/png, image/jpeg, image/webp">
                
                <% if(esEdicion && p.getBase64Imagen() != null) { %>
                    <div class="info-box mt-15" style="display: inline-flex; flex-direction: column; align-items: flex-start; padding: 15px;">
                        <img src="data:image/jpeg;base64,<%= p.getBase64Imagen() %>" class="product-img" style="width: auto; height: 100px; padding: 0;">
                        <p class="text-muted text-xs font-bold" style="margin: 10px 0 0 0;">Imagen Actual (Suba otra si desea reemplazarla)</p>
                    </div>
                <% } %>
                
                <button type="submit" class="btn-submit-tech mt-20">
                    <%= esEdicion ? "Actualizar Datos de Producto" : "Guardar Nuevo Producto" %>
                </button>
            </form>
        </div>
    </div>
</body>
</html>