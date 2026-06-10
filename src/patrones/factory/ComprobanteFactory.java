/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.factory;

/**
 *
 * @author Anderson
 */

public class ComprobanteFactory {
    
    public static IComprobante crearComprobante(String tipo) {
        if (tipo == null || tipo.isEmpty()) {
            return null;
        }
        
        if (tipo.equalsIgnoreCase("BOLETA")) {
            return new Boleta();
        } else if (tipo.equalsIgnoreCase("FACTURA")) {
            return new Factura();
        }
        
        return null;
    }
}