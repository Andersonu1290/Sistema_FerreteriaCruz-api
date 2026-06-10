/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.observer;

/**
 *
 * @author Anderson
 */

import java.util.ArrayList;
import java.util.List;

public class GestorStock {
    private List<Observador> observadores = new ArrayList<>();

    public void suscribir(Observador obs) {
        observadores.add(obs);
    }

    public void desuscribir(Observador obs) {
        observadores.remove(obs);
    }

    public List<String> notificarObservadores(String sku, int stockActual) {
        List<String> notificaciones = new ArrayList<>();
        String mensaje = "El componente SKU: " + sku + " ha alcanzado el nivel crítico de reposición. Stock actual: " + stockActual + " und.";
        
        for (Observador obs : observadores) {
            notificaciones.add(obs.actualizar(mensaje));
        }
        return notificaciones;
    }
}