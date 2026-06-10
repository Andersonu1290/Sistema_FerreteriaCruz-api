/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.dao;

/**
 *
 * @author Anderson
 */

import java.util.List;
import ferreteriacruz.modelo.Producto;

public interface IProductoDAO {
    List<Producto> listarProductos();
    boolean registrarProducto(Producto p);
    boolean actualizarProducto(Producto p);
    boolean eliminarProducto(int id);
    Producto buscarPorId(int id);
    Producto buscarPorSKU(String sku);
}