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
import java.io.IOException;
import java.util.List;
import ferreteriacruz.dao.KardexDAO;
import ferreteriacruz.modelo.MovimientoKardex;

@WebServlet(name = "KardexServlet", urlPatterns = {"/KardexServlet"})
public class KardexServlet extends HttpServlet {

    KardexDAO kDao = new KardexDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<MovimientoKardex> historial = kDao.listarHistorialKardex();
        
        request.setAttribute("historialKardex", historial);
        request.getRequestDispatcher("Kardex.jsp").forward(request, response);
    }
}