/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.strategy;

/**
 *
 * @author Anderson
 */

public class ContextoPago {
    private IEstrategiaPago estrategia;

    public void setEstrategia(IEstrategiaPago estrategia) {
        this.estrategia = estrategia;
    }

    public String ejecutarPago(String comprobante, double monto) {
        if(estrategia == null) {
            return "Error: Método de pago no seleccionado.";
        }
        return estrategia.procesarPago(comprobante, monto);
    }
}