<%-- 
    Document   : Mermas
    Created on : 10 may 2026, 10:55:25
    Author     : Anderson
--%>

<%@page import="java.util.List"%>
<%@page import="ferreteriacruz.modelo.Series"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    List<Series> disponibles = (List<Series>) request.getAttribute("seriesDisponibles");
    List<Series> mermadas = (List<Series>) request.getAttribute("seriesMermadas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FerreteriaCruz | Gestión de Mermas</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=JetBrains+Mono&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="dashboard-container">
        
        <div class="header-tech">
            <div class="header-title">
                <h2>
                    <svg viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 24px; height: 24px;">
                        <polygon points="7.86 2 16.14 2 22 7.86 22 16.14 16.14 22 7.86 22 2 16.14 2 7.86 7.86 2"></polygon>
                        <line x1="15" y1="9" x2="9" y2="15"></line>
                        <line x1="9" y1="9" x2="15" y2="15"></line>
                    </svg>
                    Gestión de Mermas (Bajas de Producto)
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

        <div class="grid-container">
            
            <div class="form-side">
                <h3 class="subtitle-blue">1. Componentes Activos (Seleccione para dar de baja)</h3>
                
                <div class="search-box-container mb-15">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#6b7280" stroke-width="2" width="16" height="16">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                    <input type="text" id="buscadorDisp" placeholder="Buscar por Nro Serie o SKU..." class="input-tech m-0">
                </div>
                
                <div class="table-panel">
                    <table class="tech-table" id="tablaDisp">
                        <thead>
                            <tr>
                                <th>CÓDIGO SKU</th>
                                <th>PRODUCTO</th>
                                <th>NÚMERO SERIE (S/N)</th>
                                <th class="text-center">ACCIÓN</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if(disponibles != null && !disponibles.isEmpty()) { 
                                   for(Series s : disponibles) { %>
                            <tr>
                                <td class="font-mono text-muted text-xs"><%= s.getCodigoSKU() %></td>
                                <td class="td-nombre"><%= s.getNombreProducto() %></td>
                                <td class="font-mono text-success font-bold"><%= s.getNumeroSerie() %></td>
                                <td class="text-center">
                                    <button type="button" onclick="ejecutarMerma('<%= s.getNumeroSerie() %>')" class="btn-action btn-delete">
                                        Mermar
                                    </button>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="4" class="text-center p-20 text-muted">No hay componentes disponibles para dar de baja</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="form-side">
                <h3 class="subtitle-red">2. Historial de Equipos Defectuosos</h3>
                
                <div class="search-box-container mb-15">
                    <svg viewBox="0 0 24 24" fill="none" stroke="#6b7280" stroke-width="2" width="16" height="16">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                    <input type="text" id="buscadorMerma" placeholder="Buscar en bajas..." class="input-tech m-0">
                </div>
                
                <div class="table-panel">
                    <table class="tech-table" id="tablaMerma">
                        <thead>
                            <tr>
                                <th>CÓDIGO SKU</th>
                                <th>PRODUCTO</th>
                                <th>NÚMERO SERIE (S/N)</th>
                                <th class="text-center">ESTADO</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if(mermadas != null && !mermadas.isEmpty()) { 
                                   for(Series m : mermadas) { %>
                            <tr class="row-disabled">
                                <td class="font-mono text-muted text-xs"><%= m.getCodigoSKU() %></td>
                                <td class="td-nombre"><%= m.getNombreProducto() %></td>
                                <td class="font-mono text-danger" style="text-decoration: line-through;"><%= m.getNumeroSerie() %></td>
                                <td class="text-center">
                                    <span class="badge badge-merma">MERMA</span>
                                </td>
                            </tr>
                            <% }} else { %>
                            <tr>
                                <td colspan="4" class="text-center p-20 text-muted">Inventario limpio (No hay mermas registradas)</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>

    <form id="frmProcesarMerma" action="MermaServlet" method="POST" style="display: none;">
        <input type="hidden" name="txtNroSerie" id="hdnNroSerie">
        <input type="hidden" name="txtMotivo" id="hdnMotivo">
    </form>

    <script src="<%=request.getContextPath()%>/assets/js/utils.js"></script>
    <script src="<%=request.getContextPath()%>/assets/js/mermas.js"></script>
</body>
</html>