<%-- 
    Document   : Reportes
    Created on : 10 may 2026, 11:08:28
    Author     : Anderson
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="ferreteriacruz.modelo.Venta"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    Map<String, Integer> kpis = (Map<String, Integer>) request.getAttribute("kpis");
    if(kpis == null) { 
        kpis = new java.util.HashMap<>(); 
        kpis.put("totalStock", 0); kpis.put("totalVentas", 0); kpis.put("totalMermas", 0); kpis.put("stockCritico", 0); 
    }
    
    int totalStock = kpis.get("totalStock");
    int totalVentas = kpis.get("totalVentas");
    int totalMermas = kpis.get("totalMermas");
    int stockCritico = kpis.get("stockCritico");
    int stockOptimo = totalStock - stockCritico;
    
    Double ingresos = (Double) request.getAttribute("ingresosTotales");
    if(ingresos == null) ingresos = 0.0;
    
    String topLabels = (String) request.getAttribute("topLabels");
    String topData = (String) request.getAttribute("topData");
    String catLabels = (String) request.getAttribute("catLabels");
    String catData = (String) request.getAttribute("catData");
    
    List<Venta> ultimasVentas = (List<Venta>) request.getAttribute("ultimasVentas");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FerreteriaCruz | Intelligence Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="dashboard-container">
        
        <div class="header-tech">
            <div class="header-title">
                <img src="<%=request.getContextPath()%>/assets/img/logo_ferreteriacruz.png" alt="FerreteriaCruz" class="logo-img">
                <h2>
                    <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="24" height="24" style="vertical-align: bottom;">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="3" y1="9" x2="21" y2="9"></line>
                        <line x1="9" y1="21" x2="9" y2="9"></line>
                    </svg>
                    Business Intelligence (BI) Dashboard
                </h2>
            </div>
            <div class="nav-links">
                <div class="user-badge" style="background: rgba(16, 185, 129, 0.1); border: 1px solid rgba(16, 185, 129, 0.3); color: #10b981;">
                    Ingresos Netos: <strong class="font-mono">S/ <%= String.format("%,.2f", ingresos) %></strong>
                </div>
                <a href="ProductoServlet?accion=listar" class="btn-tech">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
                        <line x1="19" y1="12" x2="5" y2="12"></line>
                        <polyline points="12 19 5 12 12 5"></polyline>
                    </svg>
                    Volver al Menú Principal
                </a>
            </div>
        </div>

        <div class="report-grid">
            <div class="report-card card-blue">
                <div class="report-label">Producto en Stock Total</div>
                <div class="report-value"><%= totalStock %></div>
                <span class="text-blue text-xs font-bold">↑ Unidades Físicas Activas</span>
            </div>
            <div class="report-card card-green">
                <div class="report-label">Ventas Concretadas</div>
                <div class="report-value"><%= totalVentas %></div>
                <span class="text-success text-xs font-bold">✔ Salidas Procesadas</span>
            </div>
            <div class="report-card card-red">
                <div class="report-label">Productos en Merma</div>
                <div class="report-value"><%= totalMermas %></div>
                <span class="text-danger text-xs font-bold">⚠ Equipos Defectuosos</span>
            </div>
            <div class="report-card card-yellow">
                <div class="report-label">Alertas Críticas</div>
                <div class="report-value"><%= stockCritico %></div>
                <span class="text-warning text-xs font-bold">↓ SKUs por debajo del mínimo</span>
            </div>
        </div>

        <div class="charts-container">
            <div class="chart-box" style="flex: 1.5;">
                <div class="chart-title">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                        <line x1="18" y1="20" x2="18" y2="10"></line>
                        <line x1="12" y1="20" x2="12" y2="4"></line>
                        <line x1="6" y1="20" x2="6" y2="14"></line>
                    </svg>
                    Top 5 Producto Más Vendido (Unidades Reales)
                </div>
                <div style="position: relative; height: 250px; width: 100%;">
                    <canvas id="barChart"></canvas>
                </div>
            </div>

            <div class="chart-box" style="flex: 1;">
                <div class="chart-title">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                        <path d="M21.21 15.89A10 10 0 1 1 8 2.83"></path>
                        <path d="M22 12A10 10 0 0 0 12 2v10z"></path>
                    </svg>
                    Distribución de Stock por Categoría
                </div>
                <div style="position: relative; height: 250px; width: 100%; display: flex; justify-content: center;">
                    <canvas id="doughnutChart"></canvas>
                </div>
            </div>
        </div>

        <h3 class="subtitle-blue mt-20 mb-15 d-flex align-center gap-10">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
            </svg>
            Auditoría de Ventas en Vivo (Últimas 5 transacciones)
        </h3>
        
        <div class="table-panel mb-25">
            <table class="tech-table">
                <thead>
                    <tr>
                        <th>FECHA / HORA</th>
                        <th>COMPROBANTE</th>
                        <th>CLIENTE</th>
                        <th>PRODUCTO</th>
                        <th>TOTAL</th>
                        <th class="text-center">ESTADO</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if(ultimasVentas != null && !ultimasVentas.isEmpty()) {
                            int count = 0;
                            for(Venta v : ultimasVentas) {
                                if(count >= 5) break;
                                boolean anulada = "ANULADA".equals(v.getEstado());
                    %>
                    <tr class="<%= anulada ? "row-disabled" : "" %>">
                        <td class="text-muted text-xs"><%= sdf.format(v.getFecha()) %></td>
                        <td class="font-mono text-sm"><%= v.getNroComprobante() %></td>
                        <td class="td-nombre"><%= v.getNombreCliente() %></td>
                        <td class="text-sm"><%= v.getNombreProducto() %></td>
                        <td class="font-mono text-success font-bold">S/ <%= String.format("%,.2f", v.getTotal()) %></td>
                        <td class="text-center">
                            <% if(!anulada) { %>
                                <span class="badge badge-optimal">COMPLETADA</span>
                            <% } else { %>
                                <span class="badge badge-alert">ANULADA</span>
                            <% } %>
                        </td>
                    </tr>
                    <%      count++;
                            }
                        } else { %>
                    <tr>
                        <td colspan="6" class="text-center p-30 text-muted">
                            No hay transacciones recientes para mostrar en la auditoría.
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>

    </div>

    <script src="<%=request.getContextPath()%>/assets/js/reportes.js"></script>
    <script>
        inicializarDashboard(
            [<%= request.getAttribute("topLabels") %>], 
            [<%= request.getAttribute("topData") %>], 
            [<%= request.getAttribute("catLabels") %>], 
            [<%= request.getAttribute("catData") %>]
        );
    </script>
</body>
</html>