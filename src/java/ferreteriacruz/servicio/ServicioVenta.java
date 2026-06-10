/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.servicio;

/**
 *
 * @author Anderson
 */

import ferreteriacruz.dao.VentaDAO;

public class ServicioVenta {
    private VentaDAO vDao = new VentaDAO();

    public boolean procesarSalidaHardware(int idProducto, String nroSerie, String comprobante, int idUsuario, 
                                          String docCliente, String nombreCliente, String correoCliente, 
                                          String metodoPago, double total) throws Exception {

        if (nroSerie == null || nroSerie.trim().length() < 5) {
            throw new Exception("Error de Negocio: El Número de Serie (S/N) debe tener al menos 5 caracteres.");
        }

        if (comprobante == null || comprobante.trim().isEmpty()) {
            throw new Exception("Error de Negocio: Debe generar un número de ticket válido.");
        }

        if (docCliente == null || docCliente.trim().isEmpty() || nombreCliente == null || nombreCliente.trim().isEmpty()) {
            throw new Exception("Error de Negocio: Los datos de facturación del cliente son obligatorios.");
        }

        return vDao.registrarVenta(idProducto, nroSerie, comprobante, idUsuario, docCliente, nombreCliente, correoCliente, metodoPago, total);
    }


    public java.util.List<ferreteriacruz.modelo.Venta> obtenerHistorialVentas() {
        return vDao.listarVentas();
    }

    public boolean anularVenta(int idVenta, int idUsuario) {
        return vDao.anularVenta(idVenta, idUsuario);
    }
}