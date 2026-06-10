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
import ferreteriacruz.dao.MermaDAO;
import ferreteriacruz.modelo.Usuario;

@WebServlet(name = "MermaServlet", urlPatterns = {"/MermaServlet"})
public class MermaServlet extends HttpServlet {

    MermaDAO mDao = new MermaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioActivo") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }
        
        request.setAttribute("seriesDisponibles", mDao.listarSeries("DISPONIBLE"));
        request.setAttribute("seriesMermadas", mDao.listarSeries("MERMA"));
        
        request.getRequestDispatcher("Mermas.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario user = (Usuario) session.getAttribute("usuarioActivo");
        
        if (user == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String nroSerie = request.getParameter("txtNroSerie");
        String motivo = request.getParameter("txtMotivo");
        
        mDao.procesarMerma(nroSerie, motivo, user.getIdUsuario());
        
        response.sendRedirect("MermaServlet");
    }
}