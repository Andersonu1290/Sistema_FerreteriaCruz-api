/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.strategy;

/**
 * @author Anderson
 */
public class PagoEfectivo implements IEstrategiaPago {
    @Override
    public String procesarPago(String comprobante, double monto) {
        return "Pago procesado en Efectivo (Caja) por un total de S/ " + monto + ". Ticket: " + comprobante;
    }
}