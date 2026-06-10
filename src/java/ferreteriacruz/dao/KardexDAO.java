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

import org.apache.commons.lang3.StringUtils;

import ferreteriacruz.config.Conexion;
import ferreteriacruz.modelo.MovimientoKardex;

public class KardexDAO {

    /**
     * Obtiene el historial completo de movimientos registrados
     * en el Kardex de inventario.
     *
     * Se utilizan INNER JOIN para recuperar información
     * relacionada del producto y del usuario que realizó
     * el movimiento.
     *
     * @return Lista de movimientos de Kardex.
     */
    public List<MovimientoKardex> listarHistorialKardex() {

        List<MovimientoKardex> lista = new ArrayList<>();

        String sql =
                "SELECT k.fecha, k.tipo_movimiento, " +
                "p.nombre AS producto, k.cantidad, " +
                "k.motivo, u.username " +
                "FROM kardex_movimientos k " +
                "INNER JOIN productos p " +
                "ON k.id_producto = p.id_producto " +
                "INNER JOIN usuarios u " +
                "ON k.id_usuario = u.id_usuario " +
                "ORDER BY k.fecha DESC";

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                MovimientoKardex mov = new MovimientoKardex();

                mov.setFecha(rs.getTimestamp("fecha"));

                // Evita valores nulos provenientes de la base de datos
                mov.setTipoMovimiento(
                        StringUtils.defaultString(
                                rs.getString("tipo_movimiento"))
                );

                mov.setNombreProducto(
                        StringUtils.defaultString(
                                rs.getString("producto"))
                );

                mov.setCantidad(
                        rs.getInt("cantidad")
                );

                mov.setMotivo(
                        StringUtils.defaultString(
                                rs.getString("motivo"))
                );

                mov.setNombreUsuario(
                        StringUtils.defaultString(
                                rs.getString("username"))
                );

                lista.add(mov);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error en KardexDAO: "
                    + e.getMessage()
            );
        }

        return lista;
    }
}