/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.servicio;

/**
 *
 * @author Anderson
 */

import java.util.List;
import ferreteriacruz.dao.UsuarioDAO;
import ferreteriacruz.modelo.Usuario;

public class ServicioUsuario {
    private UsuarioDAO uDao = new UsuarioDAO();

    public Usuario validarAcceso(String user, String pass) {
        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            return null;
        }
        return uDao.validarLogin(user, pass);
    }

    public List<Usuario> obtenerListaPersonal() {
        return uDao.listarUsuarios();
    }

    public boolean registrarNuevoPersonal(String user, String pass, String rol) {
        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            return false;
        }
        return uDao.registrarUsuario(user, pass, rol);
    }
}