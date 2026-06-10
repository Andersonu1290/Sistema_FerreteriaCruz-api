package ferreteriacruz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ferreteriacruz.config.Conexion;
import ferreteriacruz.modelo.Series;

// IMPORTS DE APACHE COMMONS
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class MermaDAO {

    /**
     * Obtiene las series asociadas a un estado específico.
     *
     * Se valida que el estado recibido no sea nulo ni vacío
     * y se utilizan utilidades de Apache Commons para evitar
     * valores nulos provenientes de la base de datos.
     *
     * @param estadoFiltro Estado de las series a consultar.
     * @return Lista de series encontradas.
     */
    public List<Series> listarSeries(String estadoFiltro) {

        Validate.notBlank(
                estadoFiltro,
                "El estado de búsqueda es obligatorio"
        );

        List<Series> lista = new ArrayList<>();

        String sql =
                "SELECT s.*, p.nombre, p.codigo_SKU "
                + "FROM series s "
                + "INNER JOIN productos p ON s.id_producto = p.id_producto "
                + "WHERE s.estado = ?";

        try (
                Connection con = Conexion.getInstancia().getConexion();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setString(1, estadoFiltro.trim());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Series s = new Series();

                    s.setIdSerie(rs.getInt("id_serie"));

                    s.setNumeroSerie(
                            StringUtils.defaultString(
                                    rs.getString("numero_serie"))
                    );

                    s.setIdProducto(rs.getInt("id_producto"));

                    s.setEstado(
                            StringUtils.defaultString(
                                    rs.getString("estado"))
                    );

                    s.setNombreProducto(
                            StringUtils.defaultString(
                                    rs.getString("nombre"))
                    );

                    s.setCodigoSKU(
                            StringUtils.defaultString(
                                    rs.getString("codigo_SKU"))
                    );

                    lista.add(s);
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Error listar series: "
                    + e.getMessage()
            );
        }

        return lista;
    }
    
    /**
     * Procesa una serie defectuosa (Merma).
     * Actualiza el estado de la serie, descuenta el stock del producto
     * y registra el movimiento en el Kardex.
     */
    public boolean procesarMerma(String nroSerie, String motivo, int idUsuario) {
        
        // 1. Validaciones con Apache Commons Lang 3
        Validate.notBlank(nroSerie, "El número de serie es obligatorio");
        Validate.notBlank(motivo, "El motivo de la merma es obligatorio");
        Validate.isTrue(idUsuario > 0, "ID de usuario inválido");

        boolean exito = false;
        Connection con = null;

        try {
            con = Conexion.getInstancia().getConexion();
            // Iniciamos la transacción (si falla algo, no se guarda nada)
            con.setAutoCommit(false);

            // PASO 1: Obtener el id_producto asociado a esa serie
            int idProducto = -1;
            String sqlBusca = "SELECT id_producto FROM series WHERE numero_serie = ? AND estado = 'DISPONIBLE'";
            try (PreparedStatement psBusca = con.prepareStatement(sqlBusca)) {
                psBusca.setString(1, nroSerie.trim());
                try (ResultSet rs = psBusca.executeQuery()) {
                    if (rs.next()) {
                        idProducto = rs.getInt("id_producto");
                    } else {
                        throw new Exception("La serie no existe o ya fue vendida/mermada.");
                    }
                }
            }

            // PASO 2: Cambiar el estado de la serie a 'MERMA'
            String sqlSerie = "UPDATE series SET estado = 'MERMA' WHERE numero_serie = ?";
            try (PreparedStatement psSerie = con.prepareStatement(sqlSerie)) {
                psSerie.setString(1, nroSerie.trim());
                psSerie.executeUpdate();
            }

            // PASO 3: Descontar 1 unidad del stock actual del producto
            String sqlStock = "UPDATE productos SET stock_actual = stock_actual - 1 WHERE id_producto = ?";
            try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                psStock.setInt(1, idProducto);
                psStock.executeUpdate();
            }

            // PASO 4: Registrar la salida en el Kardex
            String sqlKardex = "INSERT INTO kardex_movimientos (id_producto, tipo_movimiento, cantidad, motivo, id_usuario) VALUES (?, 'SALIDA', 1, ?, ?)";
            try (PreparedStatement psKardex = con.prepareStatement(sqlKardex)) {
                psKardex.setInt(1, idProducto);
                psKardex.setString(2, "MERMA DECLARADA: " + motivo.trim());
                psKardex.setInt(3, idUsuario);
                psKardex.executeUpdate();
            }

            // Si todo salió bien, confirmamos los cambios en MySQL
            con.commit();
            exito = true;

        } catch (Exception e) {
            // Si algo falló, revertimos todo para no corromper la base de datos
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
            System.err.println("Error crítico en procesarMerma: " + e.getMessage());
        } finally {
            // Restauramos el comportamiento por defecto de la conexión
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ex) {}
        }

        return exito;
    }

}