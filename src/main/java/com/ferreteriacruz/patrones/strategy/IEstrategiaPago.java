package com.ferreteriacruz.patrones.strategy;

public interface IEstrategiaPago {
    String procesarPago(String comprobante, double monto);
}
