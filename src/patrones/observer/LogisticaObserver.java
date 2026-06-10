/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.observer;

/**
 *
 * @author Anderson
 */

public class LogisticaObserver implements Observador {
    private String nombreResponsable;

    public LogisticaObserver(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    @Override
    public String actualizar(String mensaje) {
        return "[ALERTA LOGÍSTICA: " + nombreResponsable + "] -> " + mensaje;
    }
}