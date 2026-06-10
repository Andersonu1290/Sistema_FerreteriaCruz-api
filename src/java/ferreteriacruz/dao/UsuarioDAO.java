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
import ferreteriacruz.modelo.Usuario;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class UsuarioDAO {

    private Usuario mapearUsuario(ResultSet rs) throws Exception {

        Usuario usuario = new Usuario();

        usuario.setIdUsuario(
                rs.getInt("id_usuario")
        );

        usuario.setUsername(
                StringUtils.defaultString(
                        rs.getString("username")
                )
        );

        usuario.setPassword(
                StringUtils.defaultString(
                        rs.getString("password")
                )
        );

        usuario.setRol(
                StringUtils.defaultString(
                        rs.getString("rol")
                )
        );

        return usuario;
    }

    public Usuario validarLogin(
            String username,
            String password
    ) {

        Validate.notBlank(
                username,
                "El usuario es obligatorio"
        );

        Validate.notBlank(
                password,
                "La contraseña es obligatoria"
        );

        Usuario usuario = null;

        String sql =
                "SELECT * "
                + "FROM usuarios "
                + "WHERE username = ? "
                + "AND password = ?";

        try (
                Connection con =
                        Conexion.getInstancia()
                                .getConexion();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    username.trim()
            );

            ps.setString(
                    2,
                    password.trim()
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {
                    usuario =
                            mapearUsuario(rs);
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Error al validar login: "
                    + e.getMessage()
            );
        }

        return usuario;
    }

    public List<Usuario> listarUsuarios() {

        List<Usuario> lista =
                new ArrayList<>();

        String sql =
                "SELECT id_usuario, "
                + "username, rol "
                + "FROM usuarios "
                + "ORDER BY id_usuario DESC";

        try (
                Connection con =
                        Conexion.getInstancia()
                                .getConexion();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Usuario usuario =
                        new Usuario();

                usuario.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                usuario.setUsername(
                        StringUtils.defaultString(
                                rs.getString(
                                        "username"
                                )
                        )
                );

                usuario.setRol(
                        StringUtils.defaultString(
                                rs.getString(
                                        "rol"
                                )
                        )
                );

                lista.add(usuario);
            }

        } catch (Exception e) {

            System.err.println(
                    "Error al listar usuarios: "
                    + e.getMessage()
            );
        }

        return lista;
    }

    public boolean registrarUsuario(
            String username,
            String password,
            String rol
    ) {

        Validate.notBlank(
                username,
                "El usuario es obligatorio"
        );

        Validate.notBlank(
                password,
                "La contraseña es obligatoria"
        );

        Validate.notBlank(
                rol,
                "El rol es obligatorio"
        );

        String sql =
                "INSERT INTO usuarios "
                + "(username, password, rol) "
                + "VALUES (?, ?, ?)";

        try (
                Connection con =
                        Conexion.getInstancia()
                                .getConexion();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    username.trim()
            );

            ps.setString(
                    2,
                    password.trim()
            );

            ps.setString(
                    3,
                    rol.trim()
            );

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.err.println(
                    "Error al registrar usuario: "
                    + e.getMessage()
            );

            return false;
        }
    }
}