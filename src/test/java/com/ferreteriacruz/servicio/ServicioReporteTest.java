package com.ferreteriacruz.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import com.ferreteriacruz.repository.ProductoRepository;
import com.ferreteriacruz.repository.SeriesRepository;
import com.ferreteriacruz.repository.VentaClienteRepository;
import com.ferreteriacruz.repository.VentaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioReporteTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private VentaClienteRepository ventaClienteRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private ServicioReporte servicioReporte;

    @Test
    void testGenerarResumenEjecutivo_returnsKPIs() {
        when(productoRepository.obtenerTotalUnidadesStock()).thenReturn(100);
        when(ventaRepository.contarVentasCompletadas()).thenReturn(25);
        when(ventaClienteRepository.contarVentasCompletadasWeb()).thenReturn(5);
        when(seriesRepository.countByEstado("MERMA")).thenReturn(3L);
        when(productoRepository.contarStockCritico()).thenReturn(7);

        Map<String, Integer> kpis = servicioReporte.generarResumenEjecutivo();

        assertEquals(100, kpis.get("totalStock"));
        assertEquals(30, kpis.get("totalVentas"));
        assertEquals(3, kpis.get("totalMermas"));
        assertEquals(7, kpis.get("stockCritico"));
    }

    @Test
    void testObtenerIngresosTotales_callsRepository() {
        when(ventaRepository.obtenerTotalIngresos()).thenReturn(1234.56);
        when(ventaClienteRepository.obtenerTotalIngresosWeb()).thenReturn(100.44);
        double ingreso = servicioReporte.obtenerIngresosTotales();
        assertEquals(1335.0, ingreso);
    }

}

