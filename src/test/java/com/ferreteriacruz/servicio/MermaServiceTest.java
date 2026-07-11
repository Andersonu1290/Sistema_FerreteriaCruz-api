package com.ferreteriacruz.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.Series;
import com.ferreteriacruz.modelo.MovimientoKardex;
import com.ferreteriacruz.dao.KardexDAO;
import com.ferreteriacruz.dao.MermaDAO;
import com.ferreteriacruz.dao.ProductoDAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MermaServiceTest {

    @Mock
    private MermaDAO mermaDAO;

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private KardexDAO kardexDAO;

    @InjectMocks
    private MermaService mermaService;

    @Captor
    private ArgumentCaptor<Series> seriesCaptor;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    @Captor
    private ArgumentCaptor<MovimientoKardex> kardexCaptor;

    @Test
    void testProcesarMerma_success() throws Exception {
        String nroSerie = "SN-123";

        Series serie = new Series();
        serie.setNumeroSerie(nroSerie);
        serie.setEstado("DISPONIBLE");
        serie.setIdProducto(5);

        Producto producto = new Producto();
        producto.setIdProducto(5);
        producto.setStockActual(10);

        when(mermaDAO.findByNumeroSerieAndEstado(nroSerie, "DISPONIBLE"))
                .thenReturn(Optional.of(serie));

        when(productoDAO.findById(5)).thenReturn(Optional.of(producto));

        mermaService.procesarMerma(nroSerie, "Caducado", 2);

        verify(mermaDAO).save(seriesCaptor.capture());
        Series savedSeries = seriesCaptor.getValue();
        assertEquals("MERMA", savedSeries.getEstado());

        verify(productoDAO).save(productoCaptor.capture());
        Producto savedProducto = productoCaptor.getValue();
        assertEquals(9, savedProducto.getStockActual());

        verify(kardexDAO).save(kardexCaptor.capture());
        MovimientoKardex mov = kardexCaptor.getValue();
        assertEquals(5, mov.getIdProducto());
        assertEquals("MERMA", mov.getTipoMovimiento());
        assertEquals(1, mov.getCantidad());
        assertTrue(mov.getMotivo().contains(nroSerie));
    }
}

