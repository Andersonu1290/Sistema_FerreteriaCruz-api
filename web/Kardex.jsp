<%-- 
    Document   : Kardex
    Created on : 10 may 2026
    Author     : Grupo 2 (Ferreteria Cruz)
--%>

<%@page import="java.util.List"%>
<%@page import="ferreteriacruz.modelo.MovimientoKardex"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    List<MovimientoKardex> historial = (List<MovimientoKardex>) request.getAttribute("historialKardex");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FerreteriaCruz | Trazabilidad Kardex</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    
    <div class="dashboard-container">
        
        <div class="header-tech">
            <div class="header-title">
                <img src="<%=request.getContextPath()%>/assets/img/logo_ferreteriacruz.png" alt="FerreteriaCruz" class="logo-img" onerror="this.src='https://via.placeholder.com/150x45/111827/E63946?text=FERRETERIACRUZ'">
                <h2 class="text-white">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="24" height="24" class="text-blue">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                        <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
                        <line x1="12" y1="22.08" x2="12" y2="12"></line>
                    </svg>
                    Trazabilidad de Activos
                </h2>
            </div>
            
            <a href="ProductoServlet?accion=listar" class="btn-tech">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
                    <line x1="19" y1="12" x2="5" y2="12"></line>
                    <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Volver al Almacén
            </a>
        </div>

        <div class="search-box-container mt-15 mb-15">
            <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="20" height="20">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input type="text" id="buscadorKardex" placeholder="Buscar por Operación, S/N, Producto o Responsable..." class="input-tech">
        </div>

        <div class="table-panel">
            <table class="tech-table" id="tablaKardex">
                <thead>
                    <tr>
                        <th>FECHA / HORA</th>
                        <th>OPERACIÓN</th>
                        <th>Producto AFECTADO</th>
                        <th>CANT.</th>
                        <th>JUSTIFICACIÓN / S/N</th>
                        <th>RESPONSABLE</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (historial != null && !historial.isEmpty()) {
                            for (MovimientoKardex k : historial) {
                                
                                String claseBadge = "badge-in";
                                String claseSN = "";
                                String icono = "<line x1='12' y1='19' x2='12' y2='5'></line><polyline points='5 12 12 5 19 12'></polyline>";
                                String signo = "+";
                                String classColorCant = "text-success"; // Dinámico a clases CSS

                                if(k.getTipoMovimiento().equals("SALIDA")) { 
                                    claseBadge = "badge-out"; 
                                    icono = "<line x1='5' y1='12' x2='19' y2='12'></line><polyline points='12 5 19 12 12 19'></polyline>";
                                    signo = "-";
                                    classColorCant = "text-blue";
                                } else if(k.getTipoMovimiento().equals("MERMA")) { 
                                    claseBadge = "badge-merma"; 
                                    claseSN = "sn-red";
                                    icono = "<path d='M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z'></path><line x1='12' y1='9' x2='12' y2='13'></line><line x1='12' y1='17' x2='12.01' y2='17'></line>";
                                    signo = "-";
                                    classColorCant = "text-danger";
                                }
                    %>
                    <tr>
                        <td class="text-muted text-sm">
                            <%= k.getFecha().toString().substring(0, 10) %><br>
                            <strong class="text-white"><%= k.getFecha().toString().substring(11, 19) %></strong>
                        </td>
                        <td>
                            <span class="badge <%= claseBadge %>">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="12" height="12"><%= icono %></svg>
                                <%= k.getTipoMovimiento() %>
                            </span>
                        </td>
                        <td>
                            <div class="font-bold text-white"><%= k.getNombreProducto() %></div>
                            <% if(k.getMotivo().contains("S/N")) { %>
                                <div class="sn-box <%= claseSN %>">
                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="12" height="12" style="display: inline-block; vertical-align: middle; margin-right: 3px;">
                                        <path d="M4 5h16v14H4z"/>
                                        <line x1="8" y1="5" x2="8" y2="19"/>
                                        <line x1="16" y1="5" x2="16" y2="19"/>
                                    </svg>
                                    Trazabilidad S/N Activa
                                </div>
                            <% } %>
                        </td>
                        <td class="font-bold <%= classColorCant %>" style="font-size: 15px;">
                            <%= signo %><%= k.getCantidad() %>
                        </td>
                        <td class="text-muted text-sm" style="max-width: 250px; line-height: 1.5;">
                            <%= k.getMotivo() %>
                        </td>
                        <td>
                            <div class="user-badge m-0" style="display: inline-flex;">
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="14" height="14" class="text-muted">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                <strong><%= k.getNombreUsuario() %></strong>
                            </div>
                        </td>
                    </tr>
                    <%      }
                        } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center p-30">
                            <svg viewBox="0 0 24 24" fill="none" stroke="var(--border-tech)" stroke-width="1" width="60" height="60" class="mb-15">
                                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
                            </svg>
                            <h4 class="text-muted m-0">Kardex Vacío</h4>
                            <p class="text-muted text-sm mt-0">Aún no hay operaciones registradas en la base de datos.</p>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <script src="<%=request.getContextPath()%>/assets/js/utils.js"></script>
    <script>inicializarBuscador('buscadorKardex', 'tablaKardex');</script>
</body>
</html>