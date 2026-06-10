/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.servicio;

/**
 *
 * @author Anderson
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import ferreteriacruz.config.Conexion;
import ferreteriacruz.dao.ReporteDAO;

public class ServicioReporte implements IGeneraReporte {

    private ReporteDAO rDao = new ReporteDAO();

    @Override
    public Map<String, Integer> generarResumenEjecutivo() {
        Map<String, Integer> kpis = new HashMap<>();
        kpis.put("totalStock", 0);
        kpis.put("totalVentas", 0);
        kpis.put("totalMermas", 0);
        kpis.put("stockCritico", 0);

        Connection con = null;
        try {
            con = Conexion.getInstancia().getConexion();
            
            String sqlStock = "SELECT SUM(stock_actual) as total FROM productos";
            try (PreparedStatement ps = con.prepareStatement(sqlStock); ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getObject("total") != null) kpis.put("totalStock", rs.getInt("total"));
            }
            
            String sqlVentas = "SELECT COUNT(id_venta) as total FROM ventas WHERE estado = 'COMPLETADA'";
            try (PreparedStatement ps = con.prepareStatement(sqlVentas); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) kpis.put("totalVentas", rs.getInt("total"));
            }
            
            String sqlMermas = "SELECT COUNT(id_serie) as total FROM series WHERE estado = 'MERMA'";
            try (PreparedStatement ps = con.prepareStatement(sqlMermas); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) kpis.put("totalMermas", rs.getInt("total"));
            }
            
            String sqlCritico = "SELECT COUNT(id_producto) as total FROM productos WHERE stock_actual <= stock_minimo";
            try (PreparedStatement ps = con.prepareStatement(sqlCritico); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) kpis.put("stockCritico", rs.getInt("total"));
            }
            
        } catch (Exception e) { 
            System.err.println("Error KPI: " + e.getMessage()); 
        } finally {
            try { if(con != null) con.close(); } catch(Exception e){}
        }
        return kpis;
    }

    public double obtenerIngresosTotales() { return rDao.getTotalIngresosReales(); }
    public String[] obtenerTopProductos() { return rDao.getTopProductosReales(); }
    public String[] obtenerStockCategoria() { return rDao.getStockPorCategoriaReal(); }
}