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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import ferreteriacruz.config.Conexion;
import ferreteriacruz.modelo.Venta;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class VentaDAO {

    public boolean registrarVenta(int idProducto, String nroSerie, String comprobante, int idUsuario, 
                                  String docCliente, String nombreCliente, String correoCliente, 
                                  String metodoPago, double total) {
        
        Validate.isTrue(idProducto > 0, "El ID del producto es inválido");
        Validate.notBlank(comprobante, "El comprobante es obligatorio");
        Validate.notBlank(docCliente, "El documento del cliente es obligatorio");
        
        boolean exito = false;
        Connection con = null;
        try {
            con = Conexion.getInstancia().getConexion();
            con.setAutoCommit(false); 

            int idCliente = -1;
            String sqlBuscaCli = "SELECT id_cliente FROM clientes WHERE documento_identidad = ?";
            PreparedStatement psBuscaCli = con.prepareStatement(sqlBuscaCli);
            psBuscaCli.setString(1, docCliente);
            ResultSet rsCli = psBuscaCli.executeQuery();
            if(rsCli.next()) { idCliente = rsCli.getInt("id_cliente"); } 
            else {
                String sqlInsCli = "INSERT INTO clientes (documento_identidad, nombre_completo, correo) VALUES (?, ?, ?)";
                PreparedStatement psInsCli = con.prepareStatement(sqlInsCli, Statement.RETURN_GENERATED_KEYS);
                psInsCli.setString(1, docCliente);
                psInsCli.setString(2, nombreCliente);
                psInsCli.setString(3, StringUtils.defaultIfBlank(correoCliente, "sin_correo@ferreteriacruz.com"));
                psInsCli.executeUpdate();
                ResultSet rsKeys = psInsCli.getGeneratedKeys();
                if(rsKeys.next()) { idCliente = rsKeys.getInt(1); }
            }
            if (idCliente == -1) throw new SQLException("Fallo Cliente");

            String sqlVenta = "INSERT INTO ventas (id_cliente, id_usuario, id_producto, nro_serie, nro_comprobante, metodo_pago, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'COMPLETADA')";
            PreparedStatement psVenta = con.prepareStatement(sqlVenta);
            psVenta.setInt(1, idCliente);
            psVenta.setInt(2, idUsuario);
            psVenta.setInt(3, idProducto);
            psVenta.setString(4, nroSerie);
            psVenta.setString(5, comprobante);
            psVenta.setString(6, metodoPago);
            psVenta.setDouble(7, total);
            psVenta.executeUpdate();

            String sqlStock = "UPDATE productos SET stock_actual = stock_actual - 1 WHERE id_producto = ?";
            PreparedStatement psStock = con.prepareStatement(sqlStock);
            psStock.setInt(1, idProducto);
            psStock.executeUpdate();

            String sqlSerie = "UPDATE series SET estado = 'ASIGNADO' WHERE numero_serie = ? AND id_producto = ?";
            PreparedStatement psSerie = con.prepareStatement(sqlSerie);
            psSerie.setString(1, nroSerie);
            psSerie.setInt(2, idProducto);
            psSerie.executeUpdate();

            String sqlKardex = "INSERT INTO kardex_movimientos (id_producto, tipo_movimiento, cantidad, motivo, id_usuario) VALUES (?, 'SALIDA', 1, ?, ?)";
            PreparedStatement psKardex = con.prepareStatement(sqlKardex);
            psKardex.setInt(1, idProducto);
            psKardex.setString(2, "Ticket: " + comprobante + " | S/N asignado: " + nroSerie);
            psKardex.setInt(3, idUsuario);
            psKardex.executeUpdate();

            con.commit(); 
            exito = true;
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ex) {}
        }
        return exito;
    }

    public List<Venta> listarVentas() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, c.nombre_completo, p.nombre as producto_nombre " +
                     "FROM ventas v " +
                     "INNER JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN productos p ON v.id_producto = p.id_producto " +
                     "ORDER BY v.fecha_venta DESC";
        try {
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id_venta"));
                v.setNroComprobante(rs.getString("nro_comprobante"));
                v.setNombreCliente(rs.getString("nombre_completo"));
                v.setNombreProducto(rs.getString("producto_nombre"));
                v.setNroSerie(rs.getString("nro_serie"));
                v.setTotal(rs.getDouble("total"));
                v.setEstado(rs.getString("estado"));
                v.setFecha(rs.getTimestamp("fecha_venta"));
                lista.add(v);
            }
        } catch (Exception e) { System.err.println("Error listar: " + e.getMessage()); }
        return lista;
    }

    public boolean anularVenta(int idVenta, int idUsuario) {
        boolean exito = false;
        Connection con = null;
        try {
            con = Conexion.getInstancia().getConexion();
            con.setAutoCommit(false);
            
            String sqlGet = "SELECT id_producto, nro_serie, nro_comprobante, estado FROM ventas WHERE id_venta = ?";
            PreparedStatement psGet = con.prepareStatement(sqlGet);
            psGet.setInt(1, idVenta);
            ResultSet rs = psGet.executeQuery();
            if (!rs.next() || "ANULADA".equals(rs.getString("estado"))) {
                throw new Exception("Venta no existe o ya está anulada.");
            }
            int idProducto = rs.getInt("id_producto");
            String nroSerie = rs.getString("nro_serie");
            String comprobante = rs.getString("nro_comprobante");
            
            String sqlUpdVenta = "UPDATE ventas SET estado = 'ANULADA' WHERE id_venta = ?";
            PreparedStatement psUpdVenta = con.prepareStatement(sqlUpdVenta);
            psUpdVenta.setInt(1, idVenta);
            psUpdVenta.executeUpdate();
            

            String sqlStock = "UPDATE productos SET stock_actual = stock_actual + 1 WHERE id_producto = ?";
            PreparedStatement psStock = con.prepareStatement(sqlStock);
            psStock.setInt(1, idProducto);
            psStock.executeUpdate();
            

            String sqlSerie = "UPDATE series SET estado = 'DISPONIBLE' WHERE numero_serie = ?";
            PreparedStatement psSerie = con.prepareStatement(sqlSerie);
            psSerie.setString(1, nroSerie);
            psSerie.executeUpdate();
            
            
            String sqlKardex = "INSERT INTO kardex_movimientos (id_producto, tipo_movimiento, cantidad, motivo, id_usuario) VALUES (?, 'INGRESO', 1, ?, ?)";
            PreparedStatement psKardex = con.prepareStatement(sqlKardex);
            psKardex.setInt(1, idProducto);
            psKardex.setString(2, "ANULACIÓN DE TICKET: " + comprobante + " | S/N Reintegrado: " + nroSerie);
            psKardex.setInt(3, idUsuario);
            psKardex.executeUpdate();
            
            con.commit();
            exito = true;
        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.err.println("Error anulación: " + e.getMessage());
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ex) {}
        }
        return exito;
    }
}