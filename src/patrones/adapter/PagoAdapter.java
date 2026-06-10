/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.adapter;

/**
 *
 * @author Anderson
 */

public class PagoAdapter implements IServicioPago {
    private PasarelaExterna pasarela;

    public PagoAdapter() {
        this.pasarela = new PasarelaExterna();
    }

    @Override
    public String procesarPago(double monto) {
        return "[ADAPTER] Traduciendo... " + pasarela.realizarTransaccion(monto);
    }
}