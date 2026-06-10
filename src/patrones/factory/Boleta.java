/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.patrones.factory;

/**
 *
 * @author Anderson
 */

public class Boleta implements IComprobante {
    @Override
    public String generar(String datosVenta) {
        return "=========================================\n" +
               "   [SISTEMA FERRETERIACRUZ] - BOLETA EMITIDA  \n" +
               "   DATOS: " + datosVenta + "\n" +
               "=========================================";
    }
}