package com.ferreteriacruz.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.Series;
import com.ferreteriacruz.dao.ProductoDAO;
import com.ferreteriacruz.dao.SeriesDAO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServicioProductoTest {

    @Mock
    private ProductoDAO productoDAO;

    @Mock
    private SeriesDAO seriesDAO;

    @InjectMocks
    private ServicioProducto servicioProducto;

    @Captor
    private ArgumentCaptor<List<Series>> seriesListCaptor;

    @Test
    void testRegistrarNuevoProducto_savesProduct() {
        when(productoDAO.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setIdProducto(1);
            return p;
        });

        boolean res = servicioProducto.registrarNuevoProducto("SKU123", "Nombre");
        assertTrue(res);
        verify(productoDAO).save(any(Producto.class));
    }

    @Test
    void testGuardarProducto_new_generatesSeries() {
        Producto p = new Producto();
        p.setIdProducto(0);
        p.setCodigoSKU("SKU-A");
        p.setStockActual(3);
        p.setNombre("Producto de Prueba");

        when(productoDAO.save(any(Producto.class))).thenAnswer(inv -> {
            Producto saved = inv.getArgument(0);
            saved.setIdProducto(42);
            return saved;
        });

        boolean res = servicioProducto.guardarProducto(p);

        assertTrue(res);
        verify(seriesDAO).saveAll(seriesListCaptor.capture());
        List<Series> listas = seriesListCaptor.getValue();
        assertEquals(3, listas.size());
        for (Series s : listas) {
            assertEquals(42, s.getIdProducto());
            assertEquals("DISPONIBLE", s.getEstado());
            assertTrue(s.getNumeroSerie().startsWith("SKU-A-"));
        }
    }

}

