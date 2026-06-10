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
import ferreteriacruz.servicio.ServicioProducto;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.InputStream;

@MultipartConfig(maxFileSize = 16177215)
@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    ServicioProducto sProducto = new ServicioProducto();

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

        switch (accion) {
            case "listar":
                List<Producto> lista = sProducto.obtenerInventarioActivo();
                request.setAttribute("productos", lista);
                request.getRequestDispatcher("Inventario.jsp").forward(request, response);
                break;
                
            case "nuevo":
                
                request.setAttribute("listaCategorias", new ferreteriacruz.dao.CategoriaDAO().listarCategorias());
                request.getRequestDispatcher("ProductoForm.jsp").forward(request, response);
                break;
                
            case "editar":
                int idEditar = Integer.parseInt(request.getParameter("id"));
                Producto pEditar = sProducto.buscarProducto(idEditar);
                request.setAttribute("producto", pEditar);
                
                request.setAttribute("listaCategorias", new ferreteriacruz.dao.CategoriaDAO().listarCategorias());
                request.getRequestDispatcher("ProductoForm.jsp").forward(request, response);
                break;
                
            case "eliminar":
                int idEliminar = Integer.parseInt(request.getParameter("id"));
                sProducto.eliminarProducto(idEliminar);
                response.sendRedirect("ProductoServlet?accion=listar");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String accion = request.getParameter("accion");
        
        if ("guardar".equals(accion)) {
            Producto p = new Producto();
            
           
            String idStr = request.getParameter("txtId");
            if (idStr != null && !idStr.isEmpty()) {
                p.setIdProducto(Integer.parseInt(idStr));
            } else {
                p.setIdProducto(0); 
            }
            
            p.setCodigoSKU(request.getParameter("txtSku"));
            p.setNombre(request.getParameter("txtNombre"));
            p.setIdCategoria(Integer.parseInt(request.getParameter("cboCategoria")));
            p.setStockActual(Integer.parseInt(request.getParameter("txtStock")));
            p.setStockMinimo(Integer.parseInt(request.getParameter("txtMinimo")));
            p.setPrecio(Double.parseDouble(request.getParameter("txtPrecio")));
            Part filePart = request.getPart("txtImagen");
            if (filePart != null && filePart.getSize() > 0) {
                InputStream inputStream = filePart.getInputStream();
                byte[] imageBytes = new byte[(int) filePart.getSize()];
                inputStream.read(imageBytes);
                p.setImagen(imageBytes);
            }
            sProducto.guardarProducto(p);
            
            response.sendRedirect("ProductoServlet?accion=listar");
        }
    }
}