/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.observer;

/**
 *
 * @author Anderson
 */

public class NotificacionAdmin implements Observador {
    private String nombreAdmin;

    public NotificacionAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    @Override
    public String actualizar(String mensaje) {
        return "[ALERTA GERENCIA: " + nombreAdmin + "] -> " + mensaje;
    }
}