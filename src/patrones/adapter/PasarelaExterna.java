/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.adapter;

/**
 *
 * @author Anderson
 */

public class PasarelaExterna {
    public String realizarTransaccion(double total) {
        return "[API BANCO] Conexión segura. Transacción APROBADA por el monto de: S/ " + total;
    }
}