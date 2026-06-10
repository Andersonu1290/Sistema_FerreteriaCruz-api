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
import ferreteriacruz.modelo.Series;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class SeriesDAO {

    public boolean registrarSerie(Series serie) {

        Validate.notNull(
                serie,
                "La serie no puede ser nula"
        );

        Validate.notBlank(
                serie.getNumeroSerie(),
                "El número de serie es obligatorio"
        );

        Validate.isTrue(
                serie.getIdProducto() > 0,
                "ID de producto inválido"
        );

        String sql =
                "INSERT INTO series "
                + "(numero_serie, id_producto, estado) "
                + "VALUES (?, ?, ?)";

        try (
                Connection con = Conexion.getInstancia().getConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    serie.getNumeroSerie().trim()
            );

            ps.setInt(
                    2,
                    serie.getIdProducto()
            );

            ps.setString(
                    3,
                    StringUtils.defaultIfBlank(
                            serie.getEstado(),
                            "DISPONIBLE"
                    )
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.err.println(
                    "Error al registrar serie: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public boolean eliminarSeriesDisponibles(
            int idProducto,
            int cantidad
    ) {

        Validate.isTrue(
                idProducto > 0,
                "ID de producto inválido"
        );

        Validate.isTrue(
                cantidad > 0,
                "La cantidad debe ser mayor a cero"
        );

        String sql =
                "DELETE FROM series WHERE id_serie IN ("
                + " SELECT id FROM ("
                + " SELECT id_serie AS id "
                + " FROM series "
                + " WHERE id_producto = ? "
                + " AND estado = 'DISPONIBLE' "
                + " ORDER BY id_serie DESC "
                + " LIMIT ?"
                + " ) AS temp"
                + ")";

        try (
                Connection con = Conexion.getInstancia().getConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, idProducto);
            ps.setInt(2, cantidad);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.err.println(
                    "Error eliminando series: "
                    + e.getMessage()
            );

            return false;
        }
    }

    public List<Series> listarSeriesDisponibles(
            int idProducto
    ) {

        Validate.isTrue(
                idProducto > 0,
                "ID de producto inválido"
        );

        List<Series> lista =
                new ArrayList<>();

        String sql =
                "SELECT * FROM series "
                + "WHERE id_producto = ? "
                + "AND estado = 'DISPONIBLE'";

        try (
                Connection con =
                        Conexion.getInstancia()
                                .getConexion();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, idProducto);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    Series s =
                            new Series();

                    s.setIdSerie(
                            rs.getInt("id_serie")
                    );

                    s.setNumeroSerie(
                            StringUtils.defaultString(
                                    rs.getString(
                                            "numero_serie"
                                    )
                            )
                    );

                    s.setEstado(
                            StringUtils.defaultString(
                                    rs.getString(
                                            "estado"
                                    )
                            )
                    );

                    s.setIdProducto(
                            rs.getInt(
                                    "id_producto"
                            )
                    );

                    lista.add(s);
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Error al listar series: "
                    + e.getMessage()
            );
        }

        return lista;
    }
}