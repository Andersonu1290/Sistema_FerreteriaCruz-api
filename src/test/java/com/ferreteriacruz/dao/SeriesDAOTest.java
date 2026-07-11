package com.ferreteriacruz.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.ferreteriacruz.modelo.Categoria;
import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.Series;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;



@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SeriesDAOTest {

    @Autowired
    private SeriesDAO seriesDAO;

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Test
    void seriesQueries_returnExpectedRows() {
        String sufijo = UUID.randomUUID().toString();
        Producto producto = productoDAO.save(crearProducto("SKU-SER-1-" + sufijo, "Antibiotico-" + sufijo));
        long disponiblesAntes = seriesDAO.countByEstado("DISPONIBLE");
        seriesDAO.save(crearSeries(producto.getIdProducto(), "SER-001", "DISPONIBLE"));
        seriesDAO.save(crearSeries(producto.getIdProducto(), "SER-002", "DISPONIBLE"));
        seriesDAO.save(crearSeries(producto.getIdProducto(), "SER-003", "ASIGNADO"));

        assertEquals(2, seriesDAO.findByIdProductoAndEstado(producto.getIdProducto(), "DISPONIBLE").size());
        assertTrue(seriesDAO.findByNumeroSerieAndEstado("SER-003", "ASIGNADO").isPresent());
        assertEquals(disponiblesAntes + 2, seriesDAO.countByEstado("DISPONIBLE"));
        assertEquals(3L, seriesDAO.countByIdProducto(producto.getIdProducto()));
    }

    @Test
    void eliminarSeriesExcedentes_removesExtraDisponibleSeries() {
        String sufijo = UUID.randomUUID().toString();
        Producto producto = productoDAO.save(crearProducto("SKU-SER-2-" + sufijo, "Vitaminas-" + sufijo));
        seriesDAO.save(crearSeries(producto.getIdProducto(), "SER-101", "DISPONIBLE"));
        seriesDAO.save(crearSeries(producto.getIdProducto(), "SER-102", "DISPONIBLE"));
        seriesDAO.save(crearSeries(producto.getIdProducto(), "SER-103", "DISPONIBLE"));

        seriesDAO.eliminarSeriesExcedentes(producto.getIdProducto());

        List<Series> disponibles = seriesDAO.findByIdProductoAndEstado(producto.getIdProducto(), "DISPONIBLE");
        assertTrue(disponibles.size() <= 1);
    }

    private Producto crearProducto(String sku, String nombre) {
        Categoria categoria = categoriaDAO.save(crearCategoria("General"));
        Producto producto = new Producto();
        producto.setIdCategoria(categoria.getIdCategoria());
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(5);
        producto.setStockMinimo(1);
        producto.setPrecio(9.5);
        return producto;
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private Series crearSeries(int idProducto, String numeroSerie, String estado) {
        Series series = new Series();
        series.setIdProducto(idProducto);
        series.setNumeroSerie(numeroSerie);
        series.setEstado(estado);
        return series;
    }
}