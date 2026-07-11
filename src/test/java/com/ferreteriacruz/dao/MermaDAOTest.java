package com.ferreteriacruz.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.ferreteriacruz.modelo.Categoria;
import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.Series;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MermaDAOTest {

    @Autowired
    private MermaDAO mermaDAO;

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Test
    void mermaQueries_returnExpectedRows() {
        Producto producto = crearProducto("SKU-M1", "Crema");
        producto.setStockActual(5);
        producto = productoDAO.save(producto);
        Series merma1 = mermaDAO.save(crearSerie(producto.getIdProducto(), "MER-001", "MERMA"));
        Series merma2 = mermaDAO.save(crearSerie(producto.getIdProducto(), "MER-002", "MERMA"));
        mermaDAO.save(crearSerie(producto.getIdProducto(), "DIS-001", "DISPONIBLE"));

        List<Series> mermas = mermaDAO.findByEstado("MERMA");
        assertTrue(mermas.stream().anyMatch(serie -> "MER-001".equals(serie.getNumeroSerie())));
        assertTrue(mermas.stream().anyMatch(serie -> "MER-002".equals(serie.getNumeroSerie())));
        assertTrue(mermaDAO.findByNumeroSerieAndEstado("MER-001", "MERMA").isPresent());

        List<Object[]> filas = mermaDAO.listarSeriesConProducto("MERMA");
        assertTrue(filas.stream().anyMatch(fila -> ((Number) fila[0]).intValue() == merma1.getIdSerie()
            && "Crema".equals(fila[4])));
        assertTrue(filas.stream().anyMatch(fila -> ((Number) fila[0]).intValue() == merma2.getIdSerie()
            && "Crema".equals(fila[4])));
    }

    private Producto crearProducto(String sku, String nombre) {
        Categoria categoria = categoriaDAO.save(crearCategoria("Dermocosmetica"));
        Producto producto = new Producto();
        producto.setIdCategoria(categoria.getIdCategoria());
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(8);
        producto.setStockMinimo(2);
        producto.setPrecio(14.0);
        return producto;
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private Series crearSerie(int idProducto, String numeroSerie, String estado) {
        Series series = new Series();
        series.setIdProducto(idProducto);
        series.setNumeroSerie(numeroSerie);
        series.setEstado(estado);
        return series;
    }
}
