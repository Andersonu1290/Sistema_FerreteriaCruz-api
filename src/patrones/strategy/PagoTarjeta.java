/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.strategy;

/**
 *
 * @author Anderson
 */

import ferreteriacruz.patrones.adapter.IServicioPago;
import ferreteriacruz.patrones.adapter.PagoAdapter;

public class PagoTarjeta implements IEstrategiaPago {
    @Override
    public String procesarPago(String comprobante, double monto) {
        IServicioPago adapter = new PagoAdapter();
        String respuestaBanco = adapter.procesarPago(monto);
        return "Pago con TARJETA. " + respuestaBanco + " (Ticket: " + comprobante + ")";
    }
}
