package com.ferreteriacruz.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.ferreteriacruz.modelo.Categoria;
import com.ferreteriacruz.modelo.Producto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductoDAOTest {

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Test
    void findByCodigoSKU_returnsSavedProduct() {
        Categoria categoria = categoriaDAO.save(crearCategoria("Higiene"));
        Producto producto = productoDAO.save(crearProducto(categoria.getIdCategoria(), "SKU-100", "Alcohol", 12, 3, 8.5));

        Producto encontrado = productoDAO.findByCodigoSKU("SKU-100").orElseThrow();

        assertEquals(producto.getIdProducto(), encontrado.getIdProducto());
        assertEquals("Alcohol", encontrado.getNombre());
    }

    @Test
    void stockQueries_returnExpectedResults() {
        String sufijo = UUID.randomUUID().toString();
        Categoria categoria = categoriaDAO.save(crearCategoria("Cuidado-" + sufijo));

        int stockAntes = productoDAO.obtenerTotalUnidadesStock();
        int criticoAntes = productoDAO.contarStockCritico();
        long agotadosAntes = productoDAO.contarProductosAgotados();

        Producto jabon = productoDAO.save(crearProducto(categoria.getIdCategoria(), "SKU-200-" + sufijo, "Jabon-" + sufijo, 5, 3, 4.0));
        Producto shampoo = productoDAO.save(crearProducto(categoria.getIdCategoria(), "SKU-201-" + sufijo, "Shampoo-" + sufijo, 2, 4, 7.0));

        assertEquals(stockAntes + 7, productoDAO.obtenerTotalUnidadesStock());
        assertEquals(criticoAntes + 1, productoDAO.contarStockCritico());
        assertEquals(1L, productoDAO.buscarPorNombreOSku(jabon.getCodigoSKU()).size());
        assertEquals(agotadosAntes, productoDAO.contarProductosAgotados());

        List<Object[]> stockPorCategoria = productoDAO.obtenerStockPorCategoria();
        assertTrue(stockPorCategoria.stream().anyMatch(fila -> categoria.getNombre().equals(fila[0])
                && ((Number) fila[1]).longValue() == 7L));
    }

    @Test
    void valorInventario_returnsSumOfPriceTimesStock() {
        String sufijo = UUID.randomUUID().toString();
        Categoria categoria = categoriaDAO.save(crearCategoria("General-" + sufijo));
        double inventarioAntes = productoDAO.obtenerValorTotalInventario();

        productoDAO.save(crearProducto(categoria.getIdCategoria(), "SKU-300-" + sufijo, "Producto A-" + sufijo, 4, 1, 10.0));
        productoDAO.save(crearProducto(categoria.getIdCategoria(), "SKU-301-" + sufijo, "Producto B-" + sufijo, 6, 1, 5.0));

        assertEquals(inventarioAntes + 70.0, productoDAO.obtenerValorTotalInventario());
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private Producto crearProducto(int idCategoria, String sku, String nombre, int stockActual, int stockMinimo, double precio) {
        Producto producto = new Producto();
        producto.setIdCategoria(idCategoria);
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(stockActual);
        producto.setStockMinimo(stockMinimo);
        producto.setPrecio(precio);
        return producto;
    }
}