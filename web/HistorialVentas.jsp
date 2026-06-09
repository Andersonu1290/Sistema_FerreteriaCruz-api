<%-- 
    Document   : HistorialVentas
    Created on : 10 may 2026, 19:15:27
    Author     : Anderson
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="ferreteriacruz.modelo.Venta"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    List<Venta> lista = (List<Venta>) request.getAttribute("listaVentas");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FerreteriaCruz | Historial de Ventas</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=JetBrains+Mono&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    <div class="dashboard-container">
        
        <div class="header-tech">
            <div class="header-title">
                <h2 class="text-white">
                    <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="24" height="24">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                    </svg>
                    Auditoría e Historial de Ventas
                </h2>
            </div>
            <a href="Venta.jsp" class="btn-tech">Volver al Punto de Venta</a>
        </div>

        <div class="search-box-container">
            <svg viewBox="0 0 24 24" fill="none" stroke="#6b7280" stroke-width="2" width="20" height="20">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input type="text" id="buscadorVentas" placeholder="Buscar por Cliente, S/N, Comprobante..." class="input-tech">
        </div>

        <div class="table-panel">
            <table class="tech-table" id="tablaVentas">
                <thead>
                    <tr>
                        <th>FECHA / HORA</th>
                        <th>N° COMPROBANTE</th>
                        <th>CLIENTE</th>
                        <th>Producto (S/N)</th>
                        <th>TOTAL</th>
                        <th class="text-center">ESTADO</th>
                        <th class="text-center">ACCIÓN</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if(lista != null && !lista.isEmpty()) {
                            for(Venta v : lista) {
                                boolean anulada = "ANULADA".equals(v.getEstado());
                    %>
                    <tr class="<%= anulada ? "row-disabled" : "" %>">
                        <td class="text-muted text-sm"><%= sdf.format(v.getFecha()) %></td>
                        <td class="font-mono text-white"><%= v.getNroComprobante() %></td>
                        <td class="font-bold"><%= v.getNombreCliente() %></td>
                        <td>
                            <span class="text-blue"><%= v.getNombreProducto() %></span><br>
                            <span class="text-xs text-muted">S/N: <%= v.getNroSerie() %></span>
                        </td>
                        <td class="font-mono font-bold text-success">
                            S/ <%= String.format("%,.2f", v.getTotal()) %>
                        </td>
                        <td class="text-center">
                            <% if(!anulada) { %>
                                <span class="badge badge-optimal">COMPLETADA</span>
                            <% } else { %>
                                <span class="badge badge-alert">ANULADA</span>
                            <% } %>
                        </td>
                        <td class="text-center">
                            <% if(!anulada) { %>
                                <a href="VentaServlet?accion=anular&id=<%= v.getId() %>" 
                                   onclick="return confirm('¡ADVERTENCIA!\n\n¿Desea anular esta venta?\n\nEsto devolverá el stock al almacén, liberará el Número de Serie y creará un registro de ingreso en el Kardex. Esta acción no se puede deshacer.');" 
                                   class="btn-action btn-revert">
                                   Revertir Venta
                                </a>
                            <% } else { %>
                                <span class="text-muted text-sm">-- Reintegrado --</span>
                            <% } %>
                        </td>
                    </tr>
                    <%      }
                        } else { %>
                    <tr><td colspan="7" class="text-center p-30 text-muted">No hay transacciones registradas en el sistema.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <script src="<%=request.getContextPath()%>/assets/js/utils.js"></script>
    <script>inicializarBuscador('buscadorVentas', 'tablaVentas');</script>
</body>
</html>