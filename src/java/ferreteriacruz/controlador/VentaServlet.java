/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.controlador;

/**
 *
 * @author Anderson
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import ferreteriacruz.modelo.Producto;
import ferreteriacruz.modelo.Usuario;
import ferreteriacruz.modelo.Venta;
import ferreteriacruz.servicio.ServicioProducto;
import ferreteriacruz.servicio.ServicioVenta;

@WebServlet(name = "VentaServlet", urlPatterns = {"/VentaServlet"})
public class VentaServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
        
        if (usuarioActivo == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        ServicioVenta sVenta = new ServicioVenta();

        if ("historial".equals(accion)) {
            List<Venta> lista = sVenta.obtenerHistorialVentas();
            request.setAttribute("listaVentas", lista);
            request.getRequestDispatcher("HistorialVentas.jsp").forward(request, response);
            
        } else if ("anular".equals(accion)) {
            int idVenta = Integer.parseInt(request.getParameter("id"));
            int idUsuario = usuarioActivo.getIdUsuario();
            
            sVenta.anularVenta(idVenta, idUsuario);
            response.sendRedirect("VentaServlet?accion=historial");
        } else {
            response.sendRedirect("Venta.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
        
        if (usuarioActivo == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        try {
            int idProducto = Integer.parseInt(request.getParameter("cboProducto"));
            String nroSerie = request.getParameter("txtNroSerie");
            String comprobante = request.getParameter("txtComprobante");
            String metodoPago = request.getParameter("cboMetodoPago"); 
            String tipoComprobante = request.getParameter("cboTipoComprobante"); 
            int idUsuario = usuarioActivo.getIdUsuario();
            
            String docCliente = request.getParameter("txtDocCliente");
            String nombreCliente = request.getParameter("txtNombreCliente");
            String correoCliente = request.getParameter("txtCorreoCliente");

            ServicioProducto sProducto = new ServicioProducto();
            List<Producto> inventario = sProducto.obtenerInventarioActivo();
            double montoRealCobrar = 0.0;
            int stockRestante = 0;
            String skuVendido = "";
            int stockMinimoPermitido = 0;
            
            for (Producto p : inventario) {
                if (p.getIdProducto() == idProducto) {
                    montoRealCobrar = p.getPrecio(); 
                    stockRestante = p.getStockActual() - 1;
                    skuVendido = p.getCodigoSKU();
                    stockMinimoPermitido = p.getStockMinimo();
                    break;
                }
            }

            ferreteriacruz.patrones.strategy.ContextoPago contexto = new ferreteriacruz.patrones.strategy.ContextoPago();
            switch(metodoPago) {
                case "TARJETA": contexto.setEstrategia(new ferreteriacruz.patrones.strategy.PagoTarjeta()); break;
                case "TRANSFERENCIA": contexto.setEstrategia(new ferreteriacruz.patrones.strategy.PagoTransferencia()); break;
                default: contexto.setEstrategia(new ferreteriacruz.patrones.strategy.PagoEfectivo()); break;
            }
            String resultadoPago = contexto.ejecutarPago(comprobante, montoRealCobrar);

            ServicioVenta sVenta = new ServicioVenta();
            boolean exito = sVenta.procesarSalidaHardware(idProducto, nroSerie, comprobante, idUsuario, docCliente, nombreCliente, correoCliente, metodoPago, montoRealCobrar);

            if (exito) {
                request.setAttribute("msgPago", resultadoPago);

                ferreteriacruz.patrones.factory.IComprobante doc = ferreteriacruz.patrones.factory.ComprobanteFactory.crearComprobante(tipoComprobante);
                if (doc != null) {
                    String detalleDocumento = "CLIENTE: " + nombreCliente + " (Doc: " + docCliente + ")\n" +
                                              "CORREO: " + (correoCliente != null && !correoCliente.isEmpty() ? correoCliente : "N/A") + "\n" +
                                              "HARDWARE S/N: " + nroSerie + " | MEDIO PAGO: " + metodoPago + "\n" +
                                              "TICKET REF: " + comprobante;
                    request.setAttribute("msgDoc", doc.generar(detalleDocumento));
                }

                if (stockRestante <= stockMinimoPermitido) {
                    ferreteriacruz.patrones.observer.GestorStock gestor = new ferreteriacruz.patrones.observer.GestorStock();
                    gestor.suscribir(new ferreteriacruz.patrones.observer.LogisticaObserver("Jefe de Almacén"));
                    gestor.suscribir(new ferreteriacruz.patrones.observer.NotificacionAdmin("Gerente General"));
                    List<String> alertasGeneradas = gestor.notificarObservadores(skuVendido, stockRestante); 
                    request.setAttribute("alertasStock", alertasGeneradas);
                }
                
                request.setAttribute("tipoComprobante", tipoComprobante);
                
                request.getRequestDispatcher("ConfirmacionVenta.jsp").forward(request, response);
            } else {
                response.sendRedirect("Venta.jsp");
            }
            
        } catch (Exception e) {
            System.err.println("Excepción controlada en VentaServlet: " + e.getMessage());
            response.sendRedirect("Venta.jsp");
        }
    }
}