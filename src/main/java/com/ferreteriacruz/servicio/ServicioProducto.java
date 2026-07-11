package com.ferreteriacruz.servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.Series;
import com.ferreteriacruz.dao.ProductoDAO;
import com.ferreteriacruz.dao.SeriesDAO;
import com.google.common.base.Preconditions;

@Service
public class ServicioProducto implements IConsultaStock, IRegistroProducto {

    // Logback (vía SLF4J) - Issue #11
    private static final Logger log = LoggerFactory.getLogger(ServicioProducto.class);

    private final ProductoDAO productoDAO;
    private final SeriesDAO seriesDAO;

    // Inyección de dependencias por constructor
    public ServicioProducto(ProductoDAO productoDAO, SeriesDAO seriesDAO) {
        this.productoDAO = productoDAO;
        this.seriesDAO = seriesDAO;
    }

    @Override
    public List<Producto> obtenerInventarioActivo() {
        return productoDAO.findAll(); // Reemplaza pDao.listarProductos()
    }

    @Override
    @Transactional
    public boolean registrarNuevoProducto(String sku, String nombre) {
        Producto p = new Producto();
        p.setCodigoSKU(sku);
        p.setNombre(nombre);
        p.setIdCategoria(1);
        p.setStockActual(0);
        p.setStockMinimo(5);
        p.setPrecio(0.0);
        
        productoDAO.save(p);
        return true;
    }
    
    /**
     * Procesa inserciones y actualizaciones de productos controlando el stock.
     * @Transactional asegura que si el bucle de series falla, se cancela el guardado del producto.
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean guardarProducto(Producto p) {
        // Guava: validación defensiva de argumentos (falla rápido con mensaje claro)
        Preconditions.checkNotNull(p, "El producto no puede ser nulo");
        Preconditions.checkArgument(StringUtils.isNotBlank(p.getCodigoSKU()), "El código SKU es obligatorio");
        Preconditions.checkArgument(StringUtils.isNotBlank(p.getNombre()), "El nombre del producto es obligatorio");
        Preconditions.checkArgument(p.getStockActual() >= 0, "El stock actual no puede ser negativo");

        // Apache Commons Lang3: normaliza espacios y capitalización del nombre/SKU
        p.setCodigoSKU(StringUtils.trim(p.getCodigoSKU()).toUpperCase());
        p.setNombre(StringUtils.normalizeSpace(p.getNombre()));

        int diferenciaStock = 0;
        boolean esNuevo = (p.getIdProducto() == 0);

        log.debug("Guardando producto SKU={} nombre='{}' esNuevo={}", p.getCodigoSKU(), p.getNombre(), esNuevo);

        if (esNuevo) {
            // Guardamos el producto y recuperamos la entidad con el ID autogenerado por MySQL
            Producto productoGuardado = productoDAO.save(p);
            p.setIdProducto(productoGuardado.getIdProducto());
            diferenciaStock = p.getStockActual();
        } else {
            // Buscamos el registro anterior para calcular la diferencia exacta de stock
            Optional<Producto> prodAnteriorOpt = productoDAO.findById(p.getIdProducto());
            int stockAnterior = prodAnteriorOpt.map(Producto::getStockActual).orElse(0);
            
            productoDAO.save(p);
            diferenciaStock = p.getStockActual() - stockAnterior;
        }

        // Si el stock cambió, gestionamos las series de Producto / medicamentos en consecuencia
        if (diferenciaStock > 0) {
            generarSeriesProducto(p.getIdProducto(), p.getCodigoSKU(), diferenciaStock);
        } else if (diferenciaStock < 0) {
            removerSeriesProducto(p.getIdProducto(), Math.abs(diferenciaStock));
        }
        
        return true;
    }

    /**
     * Optimización de rendimiento: Crea una lista de series en memoria 
     * y ejecuta una sola inserción masiva (Batch Insert) usando saveAll().
     */
    private void generarSeriesProducto(int idProducto, String sku, int cantidad) {
        List<Series> listaSeries = new ArrayList<>();
        
        // 1. Averiguamos cuántas cajas de este producto ya existen históricamente en la base de datos
        long cantidadExistente = seriesDAO.countByIdProducto(idProducto);

        // 2. Creamos un "Número de Lote Fijo" (Ej: 100000 + 22 = 100022). 
        // Así SIEMPRE será el mismo número grande para este producto en específico.
        long loteFijo = 100000 + idProducto;

        for (int i = 0; i < cantidad; i++) {
            Series serie = new Series();
            serie.setIdProducto(idProducto);
            
            // 3. El correlativo continúa desde donde se quedó.
            // Si ya tenías 13 registradas, el siguiente será el 14, 15, 16... ¡Jamás volverá a haber un -0 repetido!
            long correlativo = cantidadExistente + i + 1;
            
            // Formato final: SKU - LOTE_FIJO - CORRELATIVO
            String snUnico = sku + "-" + loteFijo + "-" + correlativo;
            
            serie.setNumeroSerie(snUnico);
            serie.setEstado("DISPONIBLE");             
            listaSeries.add(serie);
        }
        
        seriesDAO.saveAll(listaSeries);
    }

    /**
     * Utiliza la consulta nativa optimizada que reparamos en el repositorio
     */
    private void removerSeriesProducto(int idProducto, int cantidad) {
        // Ejecutamos el delete transaccional por lotes usando el método del SeriesDAO
        for (int i = 0; i < cantidad; i++) {
            seriesDAO.eliminarSeriesExcedentes(idProducto);
        }
    }
    
    @Transactional
    public boolean eliminarProducto(int id) {
        if (productoDAO.existsById(id)) {
            productoDAO.deleteById(id);
            log.info("Producto id={} eliminado del inventario", id);
            return true;
        }
        log.warn("Intento de eliminar producto inexistente id={}", id);
        return false;
    }
    
    public Producto buscarProducto(int id) {
        return productoDAO.findById(id).orElse(null);
    }
}
