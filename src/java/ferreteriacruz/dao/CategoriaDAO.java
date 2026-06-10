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
import org.apache.commons.lang3.Validate;

import ferreteriacruz.config.Conexion;
import ferreteriacruz.modelo.Categoria;

public class CategoriaDAO {

    /**
     * Obtiene todas las categorías registradas en la base de datos.
     * Utiliza StringUtils para evitar valores nulos en el nombre.
     *
     * @return Lista de categorías.
     */
    public List<Categoria> listarCategorias() {

        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                // Evita valores nulos provenientes de la base de datos
                String nombre = StringUtils.defaultString(
                        rs.getString("nombre"));

                lista.add(
                    new Categoria(
                        rs.getInt("id_categoria"),
                        nombre
                    )
                );
            }

        } catch (Exception e) {
            System.err.println(
                "Error CategoriaDAO: " + e.getMessage()
            );
        }

        return lista;
    }

    /**
     * Registra una nueva categoría.
     * Se valida que el nombre no sea nulo ni vacío utilizando Apache Commons.
     *
     * @param nombre Nombre de la categoría.
     * @return true si se registró correctamente.
     */
    public boolean registrarCategoria(String nombre) {

        // Validación de entrada usando Apache Commons
        Validate.notBlank(
            nombre,
            "El nombre de la categoría es obligatorio"
        );

        String sql =
            "INSERT INTO categorias (nombre) VALUES (?)";

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            // Elimina espacios innecesarios antes de guardar
            ps.setString(1, nombre.trim());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.err.println(
                "Error registrarCategoria: "
                + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Elimina una categoría según su identificador.
     * Se valida que el ID sea mayor a cero.
     *
     * @param id Identificador de la categoría.
     * @return true si la eliminación fue exitosa.
     */
    public boolean eliminarCategoria(int id) {

        // Validación del identificador
        Validate.isTrue(
            id > 0,
            "El ID debe ser mayor que cero"
        );

        String sql =
            "DELETE FROM categorias WHERE id_categoria=?";

        try (
            Connection con = Conexion.getInstancia().getConexion();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.err.println(
                "Error eliminarCategoria: "
                + e.getMessage()
            );

            return false;
        }
    }
}