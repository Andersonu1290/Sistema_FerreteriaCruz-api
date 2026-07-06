package com.ferreteriacruz.patrones.observer;

/**
 * Evento inmutable que transporta la información de alertas para la Ferreteria Cruz.
 * Emplea la estructura nativa Record de Java 21 para omitir código repetitivo.
 */
public record StockCriticoEvent(String sku, int stockActual) {
}
