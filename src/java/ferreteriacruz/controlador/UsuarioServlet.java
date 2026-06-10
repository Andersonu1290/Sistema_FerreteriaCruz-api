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
import ferreteriacruz.modelo.Usuario;
import ferreteriacruz.servicio.ServicioUsuario;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    ServicioUsuario sUsuario = new ServicioUsuario();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioActivo") == null) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if ("listar".equals(accion) || accion == null) {

            List<Usuario> lista = sUsuario.obtenerListaPersonal();
            request.setAttribute("listaUsuarios", lista);
            request.getRequestDispatcher("Usuarios.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Usuario usuarioActivo = (Usuario) session.getAttribute("usuarioActivo");
        
        if (usuarioActivo == null || !"JEFE_ALMACEN".equals(usuarioActivo.getRol())) {
            response.sendRedirect("Login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        
        if ("registrar".equals(accion)) {
            String user = request.getParameter("txtUsername");
            String pass = request.getParameter("txtPassword");
            String rol = request.getParameter("cboRol");
            
            boolean exito = sUsuario.registrarNuevoPersonal(user, pass, rol);
            
            if (exito) {
                response.sendRedirect("UsuarioServlet?accion=listar");
            } else {
                request.setAttribute("error", "Error: Revise los campos o el 'username' ya está en uso.");
                request.setAttribute("listaUsuarios", sUsuario.obtenerListaPersonal());
                request.getRequestDispatcher("Usuarios.jsp").forward(request, response);
            }
        }
    }
}