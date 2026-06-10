/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ferreteriacruz.patrones.strategy;

/**
 *
 * @author Anderson
 */

public interface IEstrategiaPago {
    String procesarPago(String comprobante, double monto);
}