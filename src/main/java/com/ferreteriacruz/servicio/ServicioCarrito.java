package com.ferreteriacruz.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ferreteriacruz.modelo.CarritoItem;
import com.ferreteriacruz.dao.CarritoDAO;
import com.ferreteriacruz.dao.ProductoDAO;

@Service
public class ServicioCarrito {

    private final CarritoDAO carritoDAO;
    private final ProductoDAO productoDAO;

    public ServicioCarrito(CarritoDAO carritoDAO, ProductoDAO productoDAO) {
        this.carritoDAO = carritoDAO;
        this.productoDAO = productoDAO;
    }

    public List<CarritoItem> obtenerCarritoDeUsuario(int idUsuario) {
        List<CarritoItem> items = carritoDAO.findByIdUsuario(idUsuario);
        // Llenamos el objeto Producto para que Vue pueda pintar nombres, fotos y precios
        for (CarritoItem item : items) {
            productoDAO.findById(item.getIdProducto()).ifPresent(item::setProducto);
        }
        return items;
    }

    public void agregarOActualizarItem(int idUsuario, int idProducto, int cantidadAñadida) {
        Optional<CarritoItem> existente = carritoDAO.findByIdUsuarioAndIdProducto(idUsuario, idProducto);
        
        if (existente.isPresent()) {
            CarritoItem item = existente.get();
            item.setCantidad(item.getCantidad() + cantidadAñadida);
            carritoDAO.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setIdUsuario(idUsuario);
            nuevoItem.setIdProducto(idProducto);
            nuevoItem.setCantidad(cantidadAñadida);
            carritoDAO.save(nuevoItem);
        }
    }

    public void eliminarItem(int idItem) {
        carritoDAO.deleteById(idItem);
    }

    public void vaciarCarrito(int idUsuario) {
        carritoDAO.deleteByIdUsuario(idUsuario);
    }
}