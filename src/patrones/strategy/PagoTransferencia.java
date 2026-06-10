/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.strategy;

/**
 * @author Anderson
 */
public class PagoTransferencia implements IEstrategiaPago {
    @Override
    public String procesarPago(String comprobante, double monto) {
        return "Pago procesado vía YAPE/PLIN por un total de S/ " + monto + ". Ticket: " + comprobante;
    }
}