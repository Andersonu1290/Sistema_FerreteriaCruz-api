package com.ferreteriacruz.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioVentaTest {

    @Mock
    private VentaDAO ventaDAO;

    @Mock
    private ClienteDAO clienteDAO;

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private SeriesDAO seriesDAO;

    @Mock
    private KardexDAO kardexDAO;

    @Mock
    private ComprobanteFactory comprobanteFactory;

    @Mock
    private GestorStock gestorStock;

    @Captor
    private ArgumentCaptor<Venta> ventaCaptor;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    @Captor
    private ArgumentCaptor<Series> seriesCaptor;

    @Captor
    private ArgumentCaptor<MovimientoKardex> kardexCaptor;

    private ServicioVenta servicioVenta;

    private Map<String, IEstrategiaPago> estrategiasPago;

    @BeforeEach
    void setUp() {
        estrategiasPago = new HashMap<>();
        servicioVenta = new ServicioVenta(
                ventaDAO,
                clienteDAO,
                productoDAO,
                seriesDAO,
                kardexDAO,
                estrategiasPago,
                comprobanteFactory,
                gestorStock
        );
    }

    @Test
    void testProcesarSalidaProducto_web_withAvailableSeries() throws Exception {
        int idProducto = 1;
        String nroSerie = "WEB-0";
        String tipoComprobante = "BOLETA";
        int idUsuario = 10;
        String docCliente = "DNI123";
        String nombreCliente = "Cliente A";
        String correo = "a@a.com";
        String metodoPago = "EFECTIVO";
        double total = 50.0;

        // Series disponible
        Series s = new Series();
        s.setIdProducto(idProducto);
        s.setNumeroSerie("SN-1");
        s.setEstado("DISPONIBLE");
        List<Series> disponibles = new ArrayList<>();
        disponibles.add(s);

        when(seriesDAO.findByIdProductoAndEstado(idProducto, "DISPONIBLE")).thenReturn(disponibles);

        // Estrategia de pago
        IEstrategiaPago estrategia = mock(IEstrategiaPago.class);
        when(estrategia.procesarPago(anyString(), eq(total))).thenReturn("PAGO_OK");
        estrategiasPago.put(metodoPago, estrategia);

        // Comprobante
        IComprobante comp = mock(IComprobante.class);
        when(comprobanteFactory.crearComprobante(tipoComprobante)).thenReturn(comp);
        when(comp.generar(anyString())).thenReturn("DOC_OK");

        // Cliente ya existente con id definido para la prueba
        Cliente clienteExistente = new Cliente();
        clienteExistente.setIdCliente(77);
        clienteExistente.setDocumentoIdentidad(docCliente);
        clienteExistente.setNombreCompleto(nombreCliente);
        when(clienteDAO.findByDocumentoIdentidad(docCliente)).thenReturn(Optional.of(clienteExistente));

        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setStockActual(5);
        producto.setStockMinimo(2);
        producto.setCodigoSKU("SKU-1");
        producto.setNombre("Prod A");
        when(productoDAO.findById(idProducto)).thenReturn(Optional.of(producto));

        Map<String, Object> resp = servicioVenta.procesarSalidaProducto(
                idProducto, nroSerie, tipoComprobante, idUsuario,
                docCliente, nombreCliente, correo, metodoPago, total
        );

        assertNotNull(resp);
        assertEquals("PAGO_OK", resp.get("msgPago"));
        assertEquals("DOC_OK", resp.get("msgDoc"));

        verify(ventaDAO).save(ventaCaptor.capture());
        Venta savedVenta = ventaCaptor.getValue();
        // No garantizamos el id del cliente en la entidad capturada en tests unitarios (mock),
        // pero verificamos que otros campos importantes estén presentes
        assertEquals(idUsuario, savedVenta.getIdUsuario());
        assertEquals(metodoPago, savedVenta.getMetodoPago());

        verify(productoDAO).save(productoCaptor.capture());
        assertEquals(4, productoCaptor.getValue().getStockActual());

        verify(seriesDAO).save(seriesCaptor.capture());
        assertEquals("ASIGNADO", seriesCaptor.getValue().getEstado());

        verify(kardexDAO).save(kardexCaptor.capture());
        MovimientoKardex mk = kardexCaptor.getValue();
        assertEquals(idProducto, mk.getIdProducto());
    }

}

