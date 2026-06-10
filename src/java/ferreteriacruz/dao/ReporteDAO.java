/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import ferreteriacruz.config.Conexion;
import org.apache.commons.lang3.StringUtils;

public class ReporteDAO {

    public double getTotalIngresosReales() {

        double total = 0;

        String sql =
                "SELECT SUM(total) as suma "
                + "FROM ventas "
                + "WHERE estado = 'COMPLETADA'";

        try (
                Connection con = Conexion.getInstancia().getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {
                total = rs.getDouble("suma");
            }

        } catch (Exception e) {

            System.err.println(
                    "Error getTotalIngresosReales: "
                    + e.getMessage()
            );
        }

        return total;
    }

    public String[] getTopProductosReales() {

        String labels = "";
        String data = "";

        String sql =
                "SELECT p.nombre, COUNT(v.id_venta) as cantidad "
                + "FROM ventas v "
                + "INNER JOIN productos p "
                + "ON v.id_producto = p.id_producto "
                + "WHERE v.estado = 'COMPLETADA' "
                + "GROUP BY p.id_producto "
                + "ORDER BY cantidad DESC "
                + "LIMIT 5";

        try (
                Connection con = Conexion.getInstancia().getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            List<String> listLabels =
                    new ArrayList<>();

            List<String> listData =
                    new ArrayList<>();

            while (rs.next()) {

                listLabels.add(
                        "'" +
                        StringUtils.defaultString(
                                rs.getString("nombre"))
                        + "'"
                );

                listData.add(
                        StringUtils.defaultString(
                                rs.getString("cantidad"),
                                "0")
                );
            }

            labels =
                    String.join(
                            ",",
                            listLabels
                    );

            data =
                    String.join(
                            ",",
                            listData
                    );

        } catch (Exception e) {

            System.err.println(
                    "Error getTopProductosReales: "
                    + e.getMessage()
            );
        }

        return new String[]{
            labels,
            data
        };
    }

    public String[] getStockPorCategoriaReal() {

        String labels = "";
        String data = "";

        String sql =
                "SELECT c.nombre, "
                + "SUM(p.stock_actual) as total "
                + "FROM productos p "
                + "LEFT JOIN categorias c "
                + "ON p.id_categoria = c.id_categoria "
                + "GROUP BY c.id_categoria";

        try (
                Connection con = Conexion.getInstancia().getConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            List<String> listLabels =
                    new ArrayList<>();

            List<String> listData =
                    new ArrayList<>();

            while (rs.next()) {

                String nombreCategoria =
                        StringUtils.defaultIfBlank(
                                rs.getString("nombre"),
                                "Sin Categoría"
                        );

                String totalStock =
                        StringUtils.defaultIfBlank(
                                rs.getString("total"),
                                "0"
                        );

                listLabels.add(
                        "'" +
                        nombreCategoria +
                        "'"
                );

                listData.add(
                        totalStock
                );
            }

            labels =
                    String.join(
                            ",",
                            listLabels
                    );

            data =
                    String.join(
                            ",",
                            listData
                    );

        } catch (Exception e) {

            System.err.println(
                    "Error getStockPorCategoriaReal: "
                    + e.getMessage()
            );
        }

        return new String[]{
            labels,
            data
        };
    }
}