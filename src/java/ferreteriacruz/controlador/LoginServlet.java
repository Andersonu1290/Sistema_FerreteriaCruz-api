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

import ferreteriacruz.modelo.Usuario;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
       
        if ("logout".equals(accion)) {
            HttpSession session = request.getSession(false); 
            if (session != null) {
                session.invalidate();
            }

            response.sendRedirect("Login.jsp");
        } else {
            response.sendRedirect("Login.jsp");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");

        if ("ingresar".equalsIgnoreCase(accion)) {
            String user = request.getParameter("txtUser");
            String pass = request.getParameter("txtPass");

            ferreteriacruz.servicio.ServicioUsuario sUsuario = new ferreteriacruz.servicio.ServicioUsuario();
            Usuario u = sUsuario.validarAcceso(user, pass);

            if (u != null) {
                
                HttpSession session = request.getSession();
                session.setAttribute("usuarioActivo", u);
                
                response.sendRedirect("ProductoServlet?accion=listar");
            } else {
                request.setAttribute("error", "Credenciales incorrectas. Verifique su usuario y contraseña.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
            }
        }
    }
}