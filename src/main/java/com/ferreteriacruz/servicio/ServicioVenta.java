package com.ferreteriacruz.servicio;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferreteriacruz.modelo.Cliente;
import com.ferreteriacruz.modelo.MovimientoKardex;
import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.Series;
import com.ferreteriacruz.modelo.Venta;
import com.ferreteriacruz.patrones.factory.ComprobanteFactory;
import com.ferreteriacruz.patrones.factory.IComprobante;
import com.ferreteriacruz.patrones.observer.GestorStock;
import com.ferreteriacruz.patrones.strategy.IEstrategiaPago;
import com.ferreteriacruz.dao.ClienteDAO;
import com.ferreteriacruz.dao.KardexDAO;
import com.ferreteriacruz.dao.ProductoDAO;
import com.ferreteriacruz.dao.SeriesDAO;
import com.ferreteriacruz.dao.VentaDAO;

@Service
public class ServicioVenta {

    private final VentaDAO ventaDAO;
    private final ClienteDAO clienteDAO;
    private final ProductoDAO productoDAO;
    private final SeriesDAO seriesDAO;
    private final KardexDAO kardexDAO;

    private final Map<String, IEstrategiaPago> estrategiasPago;
    private final ComprobanteFactory comprobanteFactory;
    private final GestorStock gestorStock;

    public ServicioVenta(
            VentaDAO ventaDAO,
            ClienteDAO clienteDAO,
            ProductoDAO productoDAO,
            SeriesDAO seriesDAO,
            KardexDAO kardexDAO,
            Map<String, IEstrategiaPago> estrategiasPago,
            ComprobanteFactory comprobanteFactory,
            GestorStock gestorStock
    ) {
        this.ventaDAO = ventaDAO;
        this.clienteDAO = clienteDAO;
        this.productoDAO = productoDAO;
        this.seriesDAO = seriesDAO;
        this.kardexDAO = kardexDAO;
        this.estrategiasPago = estrategiasPago;
        this.comprobanteFactory = comprobanteFactory;
        this.gestorStock = gestorStock;
    }

    public List<Venta> obtenerHistorialVentas() {
        List<Object[]> resultados = ventaDAO.listarVentasConNombres();

        return resultados.stream().map(r -> {
            Venta v = new Venta();
            v.setId(((Number) r[0]).intValue());
            v.setIdCliente(((Number) r[1]).intValue());
            v.setIdUsuario(((Number) r[2]).intValue());
            v.setIdProducto(((Number) r[3]).intValue());
            v.setNroSerie((String) r[4]);
            v.setNroComprobante((String) r[5]);
            v.setMetodoPago((String) r[6]);
            v.setTotal(((Number) r[7]).doubleValue());
            
            // --- VALIDACIÓN SEGURA DE FECHA (Patrón instanceof moderno aplicado) ---
            Object fechaObjeto = r[8];
            if (fechaObjeto instanceof java.time.LocalDateTime localDateTime) {
                v.setFecha(java.sql.Timestamp.valueOf(localDateTime));
            } else if (fechaObjeto instanceof java.sql.Timestamp timestamp) {
                v.setFecha(timestamp);
            } else {
                v.setFecha((java.util.Date) fechaObjeto);
            }
            // -----------------------------------------------------------------------

            v.setEstado((String) r[9]);
            
            v.setNombreCliente((String) r[10]);
            v.setNombreProducto((String) r[11]);
            
            return v;
        }).toList();
    }

    public List<Map<String, Object>> obtenerHistorialCliente(int idUsuario) {
    
        List<Object[]> resultados =
                ventaDAO.listarMisComprasRealizadas(idUsuario);
    
        System.out.println("ID Usuario recibido: " + idUsuario);
        System.out.println("Compras encontradas: " + resultados.size());
    
        return resultados.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
        
            map.put("ticket", r[0]);
            map.put("fecha", r[1] != null ? r[1].toString() : "");
            map.put("metodoPago", r[2]);
            map.put("total", r[3]);
            map.put("estado", r[4]);
            map.put("producto", r[5]);
        
            return map;
        }).toList();
    }

    private String generarComprobante() {
        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String random = UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();

        return "TCK-" + fecha + random;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> procesarSalidaProducto(
            int idProducto,
            String nroSerie,
            String tipoComprobante, 
            int idUsuario,
            String docCliente,
            String nombreCliente,
            String correoCliente,
            String metodoPago,
            double total
    ) throws Exception {

        Series serieFisica;
        String serieFinal = nroSerie;

        if (nroSerie != null && nroSerie.startsWith("WEB-")) {
            // 🛒 LÓGICA E-COMMERCE
            List<Series> disponibles = seriesDAO.findByIdProductoAndEstado(idProducto, "DISPONIBLE");

            if (disponibles.isEmpty()) {
                // ✨ MAGIA: Si no hay series en BD pero hay stock, creamos una "Caja Virtual" al vuelo
                serieFisica = new Series();
                serieFisica.setIdProducto(idProducto);
                serieFisica.setNumeroSerie("VIRTUAL-" + System.currentTimeMillis());
                serieFisica.setEstado("DISPONIBLE"); // Se asignará en el paso final
                serieFinal = serieFisica.getNumeroSerie();
            } else {
                // Si sí hay cajas físicas registradas, toma la primera
                serieFisica = disponibles.get(0); 
                serieFinal = serieFisica.getNumeroSerie(); 
            }
        } else {
            // 🏪 LÓGICA BOTICA FÍSICA
            serieFisica = seriesDAO.findByNumeroSerieAndEstado(nroSerie, "DISPONIBLE")
                    .orElseThrow(() -> new Exception("Serie no disponible o ya vendida: " + nroSerie));
        }

        String nroComprobante = generarComprobante();

        IEstrategiaPago estrategia = estrategiasPago.get(metodoPago);
        if (estrategia == null) {
            throw new Exception("Método de pago no registrado: " + metodoPago);
        }
        String mensajePago = estrategia.procesarPago(nroComprobante, total);

        IComprobante comp = comprobanteFactory.crearComprobante(tipoComprobante);
        String mensajeDoc = "Documento no generado";
        if (comp != null) {
            String datos = "DOC: " + docCliente + " | TOTAL: S/ " + total + " | SN: " + serieFinal;
            mensajeDoc = comp.generar(datos);
        }

        Cliente cliente = clienteDAO.findByDocumentoIdentidad(docCliente)
                .orElseGet(() -> {
                    Cliente c = new Cliente(docCliente, nombreCliente);
                    c.setCorreo(correoCliente != null ? correoCliente : "sin_correo@ferreteriacruz.com");
                    return clienteDAO.save(c);
                });

        Venta venta = new Venta();
        venta.setIdCliente(cliente.getIdCliente());
        venta.setIdUsuario(idUsuario);
        venta.setIdProducto(idProducto);
        venta.setNroSerie(serieFinal); 
        venta.setNroComprobante(nroComprobante);
        venta.setMetodoPago(metodoPago);
        venta.setTotal(total);
        venta.setEstado("COMPLETADA");
        venta.setFecha(new Date());

        ventaDAO.save(venta);

        Producto producto = productoDAO.findById(idProducto)
                .orElseThrow(() -> new Exception("Producto no existe"));

        producto.setStockActual(producto.getStockActual() - 1);
        productoDAO.save(producto);

        List<String> alertasFrontend = new ArrayList<>();
        if (producto.getStockActual() <= producto.getStockMinimo()) {
            gestorStock.dispararAlertaStockCritico(producto.getCodigoSKU(), producto.getStockActual());
            alertasFrontend.add("ALERTA: El producto '" + producto.getNombre() + "' (" + producto.getCodigoSKU() + ") ha llegado a su stock mínimo (" + producto.getStockActual() + " und).");
        }

        serieFisica.setEstado("ASIGNADO");
        seriesDAO.save(serieFisica);

        MovimientoKardex kardex = new MovimientoKardex();
        kardex.setIdProducto(idProducto);
        kardex.setTipoMovimiento("SALIDA");
        kardex.setCantidad(1);
        kardex.setMotivo("Ticket: " + nroComprobante + " | SN: " + serieFinal); 
        kardex.setIdUsuario(idUsuario);
        kardex.setFecha(new Timestamp(System.currentTimeMillis()));

        kardexDAO.save(kardex);

        Map<String, Object> respuestaFinal = new HashMap<>();
        respuestaFinal.put("msgPago", mensajePago);
        respuestaFinal.put("msgDoc", mensajeDoc);
        respuestaFinal.put("tipoComprobante", tipoComprobante);
        respuestaFinal.put("comprobante", nroComprobante);
        respuestaFinal.put("total", total);
        respuestaFinal.put("nombreCliente", cliente.getNombreCompleto());
        respuestaFinal.put("alertasStock", alertasFrontend);

        return respuestaFinal;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean anularVenta(int idVenta, int idUsuario) throws Exception {

        Venta venta = ventaDAO.findById(idVenta)
                .orElseThrow(() -> new Exception("Venta no existe"));

        if ("ANULADA".equals(venta.getEstado())) {
            throw new Exception("Venta ya anulada");
        }

        venta.setEstado("ANULADA");
        ventaDAO.save(venta);

        Producto producto = productoDAO.findById(venta.getIdProducto())
                .orElseThrow(() -> new Exception("Producto no existe"));

        producto.setStockActual(producto.getStockActual() + 1);
        productoDAO.save(producto);

        Series serie = seriesDAO.findByNumeroSerieAndEstado(venta.getNroSerie(), "ASIGNADO")
                .orElseThrow(() -> new Exception("Serie no encontrada"));

        serie.setEstado("DISPONIBLE");
        seriesDAO.save(serie);

        MovimientoKardex kardex = new MovimientoKardex();
        kardex.setIdProducto(venta.getIdProducto());
        kardex.setTipoMovimiento("INGRESO");
        kardex.setCantidad(1);
        kardex.setMotivo("ANULACIÓN TICKET: " + venta.getNroComprobante());
        kardex.setIdUsuario(idUsuario);
        kardex.setFecha(new Timestamp(System.currentTimeMillis()));

        kardexDAO.save(kardex);

        return true;
    }

    // Buscador rápido de clientes para el autocompletado en caja
    public Cliente buscarClientePorDni(String dni) {
        return clienteDAO.findByDocumentoIdentidad(dni).orElse(null);
    }
}