<%-- 
    Document   : Venta
    Created on : 11 may 2026, 11:12:10
    Author     : Grupo 2 (Ferreteria Cruz)
--%>

<%@page import="java.util.List"%>
<%@page import="ferreteriacruz.modelo.Producto"%>
<%@page import="ferreteriacruz.servicio.ServicioProducto"%>
<%@page import="ferreteriacruz.modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActivo == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    ServicioProducto sProducto = new ServicioProducto();
    List<Producto> listaProductos = sProducto.obtenerInventarioActivo();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FerreteriaCruz | Registro de Salidas</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=JetBrains+Mono:wght@500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/estilos.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/assets/img/favicon.ico" type="image/x-icon">
</head>
<body>
    
    <div class="dashboard-container-narrow">
        
        <div class="header-tech">
            <div class="header-title">
                <img src="<%=request.getContextPath()%>/assets/img/logo_ferreteriacruz.png" alt="FerreteriaCruz" class="logo-img" onerror="this.src='https://via.placeholder.com/150x45/111827/E63946?text=FERRETERIACRUZ'">
                <h2>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="24" height="24" class="text-blue">
                        <circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                    </svg>
                    Terminal de Salida / Venta
                </h2>
            </div>
            <div class="nav-links">
                <a href="VentaServlet?accion=historial" class="btn-tech btn-primary-tech">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
                        <circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                    Historial y Anulaciones
                </a>
                <a href="ProductoServlet?accion=listar" class="btn-tech">
                    Volver al Inventario
                </a>
            </div>
        </div>

        <div class="info-box mb-25">
            <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="20" height="20">
                <circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
            <p class="text-muted text-sm m-0">Proceso Transaccional: El stock será descontado inmediatamente en MySQL y reportado al Kardex. Cantidad bloqueada a 1 para garantizar la Trazabilidad del Número de Serie.</p>
        </div>

        <div class="form-panel">
            <h3 class="mb-25">
                <svg viewBox="0 0 24 24" fill="none" stroke="var(--brand-blue)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="18" height="18">
                    <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect><line x1="8" y1="21" x2="16" y2="21"></line><line x1="12" y1="17" x2="12" y2="21"></line>
                </svg>
                Asignación de Activo (Producto)
            </h3>
            
            <form action="VentaServlet" method="POST">
                
                <label for="cboProducto" class="form-label">Componente a despachar (SKU):</label>
                <select id="cboProducto" name="cboProducto" class="input-tech" required>
                    <option value="" disabled selected>-- Inicialice escaneo o seleccione de la lista --</option>
                    <%
                        ferreteriacruz.dao.SeriesDAO serieDao = new ferreteriacruz.dao.SeriesDAO();
                        if (listaProductos != null) {
                            for (Producto p : listaProductos) {
                                if (p.getStockActual() > 0) {
                                    java.util.List<ferreteriacruz.modelo.Series> seriesDisp = serieDao.listarSeriesDisponibles(p.getIdProducto());
                                    StringBuilder sbSeries = new StringBuilder();
                                    for(ferreteriacruz.modelo.Series s : seriesDisp) {
                                        sbSeries.append(s.getNumeroSerie()).append(",");
                                    }
                                    String strSeries = sbSeries.toString();
                                    if(strSeries.length() > 0) strSeries = strSeries.substring(0, strSeries.length() - 1);
                    %>
                                    <option value="<%= p.getIdProducto() %>" data-series="<%= strSeries %>">
                                        [<%= p.getCodigoSKU() %>] - <%= p.getNombre() %> (Stock disp: <%= p.getStockActual() %>)
                                    </option>
                    <%
                                }
                            }
                        }
                    %>
                </select>

                <label for="txtNroSerie" class="form-label">Número de Serie (S/N):</label>
                <select id="txtNroSerie" name="txtNroSerie" class="input-tech input-mono" required>
                    <option value="" disabled selected>-- Primero seleccione un componente --</option>
                </select>

                <div class="d-flex gap-15 mb-25" style="flex-wrap: wrap;">
                    
                    <div class="flex-col-0-8">
                        <label for="txtCantidad" class="form-label">Cant.</label>
                        <input type="number" id="txtCantidad" name="txtCantidad" class="input-tech input-readonly input-mono m-0" value="1" min="1" required readonly>
                    </div>
                    
                    <div class="flex-col-1-5">
                        <label for="cboTipoComprobante" class="form-label">Tipo de Doc:</label>
                        <select id="cboTipoComprobante" name="cboTipoComprobante" class="input-tech m-0" required>
                            <option value="BOLETA">Boleta</option>
                            <option value="FACTURA">Factura</option>
                        </select>
                    </div>

                    <div class="flex-col-1-5">
                        <label for="txtComprobante" class="form-label">Nro. de Ticket:</label>
                        <input type="text" id="txtComprobante" name="txtComprobante" class="input-tech input-mono m-0" required placeholder="Ej. TCK-00452">
                    </div>

                    <div class="flex-col-1-5">
                        <label for="cboMetodoPago" class="form-label">Método de Pago:</label>
                        <div class="payment-selector-container">
                            <label class="payment-option">
                                <input type="radio" name="cboMetodoPago" value="EFECTIVO" checked>
                                <div class="payment-content">
                                    <img src="assets/img/icono-efectivo.png" alt="Efectivo">
                                    <span>Efectivo</span>
                                </div>
                            </label>

                            <label class="payment-option">
                                <input type="radio" name="cboMetodoPago" value="TARJETA">
                                <div class="payment-content">
                                    <img src="assets/img/icono-tarjeta.png" alt="Tarjeta">
                                    <span>Tarjeta</span>
                                </div>
                            </label>

                            <label class="payment-option">
                                <input type="radio" name="cboMetodoPago" value="TRANSFERENCIA">
                                <div class="payment-content">
                                    <img src="assets/img/icono-transfer.png" alt="Transferencia">
                                    <span>Transfer.</span>
                                </div>
                            </label>
                        </div>
                    </div>

                </div>

                <div class="info-box mt-20 p-20" style="display: block; border-left-color: var(--brand-blue);">
                    <h4 class="text-blue text-sm mb-15 mt-0 font-bold">Datos del Cliente (Facturación)</h4>
                    
                    <div class="d-flex gap-15" style="flex-wrap: wrap;">
                        <div class="flex-col">
                            <label class="form-label">DNI / RUC:</label>
                            <input type="text" name="txtDocCliente" class="input-tech input-mono m-0" required placeholder="Ej. 70123456">
                        </div>
                        <div class="flex-col-1-5">
                            <label class="form-label">Nombre o Razón Social:</label>
                            <input type="text" name="txtNombreCliente" class="input-tech m-0" required placeholder="Ej. Juan Pérez">
                        </div>
                        <div class="flex-col-1-5">
                            <label class="form-label">Correo Electrónico:</label>
                            <input type="email" name="txtCorreoCliente" class="input-tech m-0" placeholder="correo@empresa.com">
                        </div>
                    </div>
                </div>
                
                <button type="submit" class="btn-submit-tech mt-20">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="20" height="20">
                        <line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                    Confirmar Salida de Inventario
                </button>
            </form>
        </div>

    </div>

    <script src="assets/js/venta.js"></script>
</body>
</html>