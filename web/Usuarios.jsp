<%-- 
    Document   : Usuarios
    Created on : 10 may 2026, 10:51:48
    Author     : Anderson
--%>

<%@page import="java.util.List"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    boolean esAdmin = "JEFE_ALMACEN".equals(usuarioActivo.getRol());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FerreteriaCruz | Gestión de Usuarios</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=JetBrains+Mono&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="dashboard-container">
        
        <div class="header-tech">
            <div class="header-title">
                <h2>
                    <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 24px; height: 24px;">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                    </svg>
                    Gestión de Personal (Módulo de Seguridad)
                </h2>
            </div>
            <a href="ProductoServlet?accion=listar" class="btn-tech">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
                    <line x1="19" y1="12" x2="5" y2="12"></line>
                    <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Volver al Inventario
            </a>
        </div>

        <% if(request.getAttribute("error") != null) { %>
            <div class="alert-error mb-15">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                <span><%= request.getAttribute("error") %></span>
            </div>
        <% } %>

        <div class="grid-container">
            
            <div class="form-side">
                <h3 class="subtitle-blue">Registrar Nuevo Operario</h3>
                
                <% if(esAdmin) { %>
                    <form action="UsuarioServlet?accion=registrar" method="POST">
                        <label for="txtUsername" class="form-label">Nombre de Usuario (Login):</label>
                        <input type="text" id="txtUsername" name="txtUsername" class="input-tech" required autocomplete="off" placeholder="Ej. jperez">

                        <label for="txtPassword" class="form-label">Contraseña de acceso:</label>
                        <input type="password" id="txtPassword" name="txtPassword" class="input-tech" required placeholder="••••••••">

                        <label for="cboRol" class="form-label">Rol en el Sistema:</label>
                        <select id="cboRol" name="cboRol" class="input-tech" required>
                            <option value="ALMACEN">Operario de Almacén</option>
                            <option value="JEFE_ALMACEN">Jefe de Almacén (Admin)</option>
                        </select>

                        <button type="submit" class="btn-submit-tech mt-15">
                            Guardar Credenciales
                        </button>
                    </form>
                <% } else { %>
                    <div class="note-box mt-20">
                        <h4 class="text-warning d-flex align-center gap-10 mb-15 mt-0 text-sm">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                                <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                                <line x1="12" y1="9" x2="12" y2="13"></line>
                                <line x1="12" y1="17" x2="12.01" y2="17"></line>
                            </svg>
                            Acceso Denegado
                        </h4>
                        <p class="text-muted text-xs">Solo el perfil <strong>'JEFE_ALMACEN'</strong> puede registrar nuevos usuarios en el sistema.</p>
                    </div>
                <% } %>
            </div>

            <div class="table-side">
                <div class="d-flex justify-between align-center mb-15">
                    <h3 class="text-white font-bold m-0">Personal Autorizado</h3>
                </div>
                
                <div class="search-box-container mb-15">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#6b7280" stroke-width="2" width="16" height="16">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                    <input type="text" id="buscadorUsuarios" placeholder="Buscar usuario o rol..." class="input-tech m-0" style="max-width: none;">
                </div>
                
                <div class="table-panel">
                    <table class="tech-table" id="tablaUsuarios">
                        <thead>
                            <tr>
                                <th>ID SYS</th>
                                <th>Nombre de Usuario</th>
                                <th class="text-center">Rol de Seguridad</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<Usuario> lista = (List<Usuario>) request.getAttribute("listaUsuarios");
                                if (lista != null && !lista.isEmpty()) {
                                    for (Usuario u : lista) {
                                        String badgeClase = u.getRol().equals("JEFE_ALMACEN") ? "badge-role-admin" : "badge-role-user";
                            %>
                                <tr>
                                    <td class="td-id">USR-<%= String.format("%03d", u.getIdUsuario()) %></td>
                                    <td class="font-bold text-white"><%= u.getUsername() %></td>
                                    <td class="text-center">
                                        <span class="badge <%= badgeClase %>">
                                            <%= u.getRol() %>
                                        </span>
                                    </td>
                                </tr>
                            <%
                                    }
                                } else {
                            %>
                                <tr><td colspan="3" class="text-center p-30 text-muted">No hay registros disponibles.</td></tr>
                            <%  } %>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
                        
    <script src="<%=request.getContextPath()%>/assets/js/utils.js"></script>
    <script>inicializarBuscador('buscadorUsuarios', 'tablaUsuarios');</script>
</body>
</html>