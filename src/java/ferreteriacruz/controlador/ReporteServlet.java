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
import java.util.Map;

import ferreteriacruz.servicio.ServicioReporte;
import ferreteriacruz.dao.VentaDAO;

@WebServlet(name = "ReporteServlet", urlPatterns = {"/ReporteServlet"})
public class ReporteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioActivo") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        ServicioReporte generador = new ServicioReporte();

        Map<String, Integer> kpis = generador.generarResumenEjecutivo();

        double ingresos = generador.obtenerIngresosTotales();
        String[] topProd = generador.obtenerTopProductos();
        String[] catStock = generador.obtenerStockCategoria();
        
        request.setAttribute("kpis", kpis);
        request.setAttribute("ingresosTotales", ingresos);
        request.setAttribute("topLabels", topProd[0]);
        request.setAttribute("topData", topProd[1]);
        request.setAttribute("catLabels", catStock[0]);
        request.setAttribute("catData", catStock[1]);
        
        request.setAttribute("ultimasVentas", new VentaDAO().listarVentas());
        
        request.getRequestDispatcher("Reportes.jsp").forward(request, response);
    }
}