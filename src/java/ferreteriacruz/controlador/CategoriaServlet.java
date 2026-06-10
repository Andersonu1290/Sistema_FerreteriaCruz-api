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
import ferreteriacruz.dao.CategoriaDAO;
import ferreteriacruz.modelo.Categoria;

@WebServlet(name = "CategoriaServlet", urlPatterns = {"/CategoriaServlet"})
public class CategoriaServlet extends HttpServlet {

    CategoriaDAO cDao = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioActivo") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        if ("listar".equals(accion)) {
            List<Categoria> lista = cDao.listarCategorias();
            request.setAttribute("listaCategorias", lista);
            request.getRequestDispatcher("Categorias.jsp").forward(request, response);
        } else if ("eliminar".equals(accion)) {
            int id = Integer.parseInt(request.getParameter("id"));
            cDao.eliminarCategoria(id);
            response.sendRedirect("CategoriaServlet?accion=listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        if ("guardar".equals(accion)) {
            String nombre = request.getParameter("txtNombre");
            cDao.registrarCategoria(nombre);
            response.sendRedirect("CategoriaServlet?accion=listar");
        }
    }
}