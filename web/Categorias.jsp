<%-- 
    Document   : Categorias
    Created on : 10 may 2026, 18:53:31
    Author     : Anderson
--%>

<%@page import="java.util.List"%>
<%@page import="ferreteriacruz.modelo.Categoria"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    List<Categoria> lista = (List<Categoria>) request.getAttribute("listaCategorias");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FerreteriaCruz | Gestión de Categorías</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=JetBrains+Mono&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="dashboard-container">
        
        <div class="header-tech">
            <div class="header-title">
                <h2 class="page-title text-white">
                    <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="24" height="24">
                        <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path>
                    </svg>
                    Categorías de Producto
                </h2>
            </div>
            <a href="ProductoServlet?accion=nuevo" class="btn-tech">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
                    <line x1="19" y1="12" x2="5" y2="12"></line>
                    <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Volver a Registro de Producto
            </a>
        </div>

        <div class="grid-container mt-20">
            
            <div class="form-side">
                <h3 class="subtitle-blue">Crear Nueva Familia</h3>
                
                <form action="CategoriaServlet?accion=guardar" method="POST">
                    <label class="form-label">Nombre de la Categoría:</label>
                    <input type="text" name="txtNombre" class="input-tech" required placeholder="Ej. Monitores, Fuentes de Poder..." autocomplete="off">
                    
                    <button type="submit" class="btn-submit-tech">
                        Guardar Categoría
                    </button>
                </form>
                
                <div class="note-box mt-20">
                    <p>
                        <strong>Nota:</strong> Las categorías creadas aquí aparecerán automáticamente en el menú desplegable al registrar un nuevo componente en el inventario.
                    </p>
                </div>
            </div>

            <div class="table-side">
                <div class="d-flex justify-between align-center mb-15">
                    <h3 class="text-white font-bold">Familias Registradas</h3>
                </div>

                <div class="search-box-container">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#6b7280" stroke-width="2" width="16" height="16">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                    <input type="text" id="buscadorCat" class="input-tech" placeholder="Buscar familia...">
                </div>

                <div class="table-panel">
                    <table class="tech-table" id="tablaCat">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>NOMBRE DE FAMILIA / CATEGORÍA</th>
                                <th class="text-center">ACCIONES</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if(lista != null && !lista.isEmpty()) {
                                    for(Categoria c : lista) {
                            %>
                            <tr>
                                <td class="td-id">#<%= String.format("%03d", c.getIdCategoria()) %></td>
                                <td class="td-nombre"><%= c.getNombre() %></td>
                                <td class="text-center">
                                    <a href="CategoriaServlet?accion=eliminar&id=<%= c.getIdCategoria() %>" class="btn-action btn-delete" onclick="return confirm('¿Eliminar la categoría <%= c.getNombre() %>? Asegúrese de que no haya productos usándola.');">Eliminar</a>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="3" class="text-center p-30">
                                    <p class="text-muted text-sm">No hay categorías registradas.</p>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>

    <script src="<%=request.getContextPath()%>/assets/js/utils.js"></script>
    <script>inicializarBuscador('buscadorCat', 'tablaCat');</script>
</body>
</html>