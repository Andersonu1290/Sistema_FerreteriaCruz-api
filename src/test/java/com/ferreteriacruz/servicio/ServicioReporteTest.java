package com.ferreteriacruz.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import com.ferreteriacruz.dao.ProductoDAO;
import com.ferreteriacruz.dao.SeriesDAO;
import com.ferreteriacruz.dao.VentaClienteDAO;
import com.ferreteriacruz.dao.VentaDAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioReporteTest {

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private VentaDAO ventaDAO;

    @Mock
    private VentaClienteDAO ventaClienteDAO;

    @Mock
    private SeriesDAO seriesDAO;

    @InjectMocks
    private ServicioReporte servicioReporte;

    @Test
    void testGenerarResumenEjecutivo_returnsKPIs() {
        when(productoDAO.obtenerTotalUnidadesStock()).thenReturn(100);
        when(ventaDAO.contarVentasCompletadas()).thenReturn(25);
        when(ventaClienteDAO.contarVentasCompletadasWeb()).thenReturn(5);
        when(seriesDAO.countByEstado("MERMA")).thenReturn(3L);
        when(productoDAO.contarStockCritico()).thenReturn(7);

        Map<String, Integer> kpis = servicioReporte.generarResumenEjecutivo();

        assertEquals(100, kpis.get("totalStock"));
        assertEquals(30, kpis.get("totalVentas"));
        assertEquals(3, kpis.get("totalMermas"));
        assertEquals(7, kpis.get("stockCritico"));
    }

    @Test
    void testObtenerIngresosTotales_callsDAO() {
        when(ventaDAO.obtenerTotalIngresos()).thenReturn(1234.56);
        when(ventaClienteDAO.obtenerTotalIngresosWeb()).thenReturn(100.44);
        double ingreso = servicioReporte.obtenerIngresosTotales();
        assertEquals(1335.0, ingreso);
    }

}

