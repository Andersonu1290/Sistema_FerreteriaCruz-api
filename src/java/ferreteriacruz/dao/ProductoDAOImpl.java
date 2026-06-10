/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.dao;

/**
 *
 * @author Anderson
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import ferreteriacruz.config.Conexion;
import ferreteriacruz.modelo.Producto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;


public class ProductoDAOImpl implements IProductoDAO {

    private Producto mapearProducto(ResultSet rs) throws Exception {

    Producto p = new Producto();

    p.setIdProducto(rs.getInt("id_producto"));

    p.setCodigoSKU(
            StringUtils.defaultString(
                    rs.getString("codigo_SKU"))
    );

    p.setNombre(
            StringUtils.defaultString(
                    rs.getString("nombre"))
    );

    p.setIdCategoria(rs.getInt("id_categoria"));
    p.setStockActual(rs.getInt("stock_actual"));
    p.setStockMinimo(rs.getInt("stock_minimo"));
    p.setPrecio(rs.getDouble("precio"));
    p.setImagen(rs.getBytes("imagen"));

    return p;
}

    @Override
   public List<Producto> listarProductos() {

    List<Producto> lista = new ArrayList<>();
    String sql = "SELECT * FROM productos";

    try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
    ) {

        while (rs.next()) {
            lista.add(mapearProducto(rs));
        }

    } catch (Exception e) {
        System.err.println("Error listar: " + e.getMessage());
    }

    return lista;
}
    @Override
public boolean registrarProducto(Producto p) {

    Validate.notNull(
            p,
            "El producto no puede ser nulo"
    );

    Validate.notBlank(
            p.getCodigoSKU(),
            "El SKU es obligatorio"
    );

    Validate.notBlank(
            p.getNombre(),
            "El nombre es obligatorio"
    );

    Validate.isTrue(
            p.getPrecio() >= 0,
            "El precio no puede ser negativo"
    );

    String sql = "INSERT INTO productos (codigo_SKU, nombre, id_categoria, stock_actual, stock_minimo, precio, imagen) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, p.getCodigoSKU().trim());
        ps.setString(2, p.getNombre().trim());
        ps.setInt(3, p.getIdCategoria());
        ps.setInt(4, p.getStockActual());
        ps.setInt(5, p.getStockMinimo());
        ps.setDouble(6, p.getPrecio());
        ps.setBytes(7, p.getImagen());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {

        System.err.println("Error registrar: " + e.getMessage());
        return false;
    }
}

@Override
public boolean actualizarProducto(Producto p) {

    Validate.notNull(
            p,
            "Producto inválido"
    );

    Validate.isTrue(
            p.getIdProducto() > 0,
            "ID de producto inválido"
    );

    String sql;

    boolean actualizaImagen =
            p.getImagen() != null
            && p.getImagen().length > 0;

    if (actualizaImagen) {

        sql = "UPDATE productos "
                + "SET codigo_SKU=?, nombre=?, "
                + "id_categoria=?, stock_actual=?, "
                + "stock_minimo=?, precio=?, imagen=? "
                + "WHERE id_producto=?";

    } else {

        sql = "UPDATE productos "
                + "SET codigo_SKU=?, nombre=?, "
                + "id_categoria=?, stock_actual=?, "
                + "stock_minimo=?, precio=? "
                + "WHERE id_producto=?";
    }

    try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, p.getCodigoSKU().trim());
        ps.setString(2, p.getNombre().trim());
        ps.setInt(3, p.getIdCategoria());
        ps.setInt(4, p.getStockActual());
        ps.setInt(5, p.getStockMinimo());
        ps.setDouble(6, p.getPrecio());

        if (actualizaImagen) {

            ps.setBytes(7, p.getImagen());
            ps.setInt(8, p.getIdProducto());

        } else {

            ps.setInt(7, p.getIdProducto());
        }

        return ps.executeUpdate() > 0;

    } catch (Exception e) {

        System.err.println(
                "Error actualizar: "
                + e.getMessage()
        );

        return false;
    }
}


@Override
public boolean eliminarProducto(int id) {

    // 1. Validación estricta con Apache Commons
    Validate.isTrue(
            id > 0,
            "El ID debe ser mayor a cero"
    );

    String sql = "DELETE FROM productos WHERE id_producto=?";

    try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
    ) {
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        System.err.println("Error al eliminar producto: " + e.getMessage());
        return false;
    }
}

    @Override
public Producto buscarPorId(int id) {

    Validate.isTrue(
            id > 0,
            "ID inválido"
    );

    Producto p = null;

    String sql =
            "SELECT * FROM productos WHERE id_producto=?";

    try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                p = mapearProducto(rs);
            }
        }

    } catch (Exception e) {

        System.err.println(
                "Error buscar: "
                + e.getMessage()
        );
    }

    return p;
}
    
   @Override
public Producto buscarPorSKU(String sku) {

    Validate.notBlank(
            sku,
            "El SKU es obligatorio"
    );

    Producto p = null;

    String sql =
            "SELECT * FROM productos WHERE codigo_SKU=?";

    try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
    ) {

        ps.setString(1, sku.trim());

        try (ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                p = mapearProducto(rs);
            }
        }

    } catch (Exception e) {

        System.err.println(
                "Error buscarPorSKU: "
                + e.getMessage()
        );
    }

    return p;
}
}