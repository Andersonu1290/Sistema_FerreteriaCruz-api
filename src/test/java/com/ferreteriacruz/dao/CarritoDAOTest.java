package com.ferreteriacruz.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import com.ferreteriacruz.modelo.CarritoItem;
import com.ferreteriacruz.modelo.Categoria;
import com.ferreteriacruz.modelo.Producto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarritoDAOTest {

    @Autowired
    private CarritoDAO carritoDAO;

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Test
    void findAndDeleteByUsuario_workAsExpected() {

        // Limpiar posibles datos existentes de pruebas anteriores
        carritoDAO.deleteByIdUsuario(12);
        carritoDAO.deleteByIdUsuario(13);

        Producto producto1 = productoDAO.save(
                crearProducto("SKU-CAR-1-" + UUID.randomUUID(), "Producto Carrito 1"));

        Producto producto2 = productoDAO.save(
                crearProducto("SKU-CAR-2-" + UUID.randomUUID(), "Producto Carrito 2"));

        carritoDAO.save(crearItem(12, producto1.getIdProducto(), 2));
        carritoDAO.save(crearItem(12, producto2.getIdProducto(), 1));
        carritoDAO.save(crearItem(13, producto1.getIdProducto(), 5));

        List<CarritoItem> items = carritoDAO.findByIdUsuario(12);
        assertEquals(2, items.size());

        assertTrue(
                carritoDAO.findByIdUsuarioAndIdProducto(12, producto1.getIdProducto()).isPresent());

        carritoDAO.deleteByIdUsuario(12);

        assertEquals(0, carritoDAO.findByIdUsuario(12).size());
        assertEquals(1, carritoDAO.findByIdUsuario(13).size());
    }

    private Producto crearProducto(String sku, String nombre) {
        Categoria categoria = categoriaDAO.save(
                crearCategoria("Accesorios-" + UUID.randomUUID()));

        Producto producto = new Producto();
        producto.setIdCategoria(categoria.getIdCategoria());
        producto.setCodigoSKU(sku);
        producto.setNombre(nombre);
        producto.setStockActual(10);
        producto.setStockMinimo(1);
        producto.setPrecio(6.0);

        return producto;
    }

    private Categoria crearCategoria(String nombre) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        return categoria;
    }

    private CarritoItem crearItem(int idUsuario, int idProducto, int cantidad) {
        CarritoItem item = new CarritoItem();
        item.setIdUsuario(idUsuario);
        item.setIdProducto(idProducto);
        item.setCantidad(cantidad);
        return item;
    }
}