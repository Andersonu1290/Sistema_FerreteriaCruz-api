package com.ferreteriacruz.servicio;

import com.ferreteriacruz.modelo.Series;
import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.MovimientoKardex;
import com.ferreteriacruz.dao.MermaDAO;
import com.ferreteriacruz.dao.ProductoDAO;
import com.ferreteriacruz.dao.KardexDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class MermaService {

    private final MermaDAO mermaDAO;
    private final ProductoDAO productoDAO;
    private final KardexDAO kardexDAO;

    // Inyección de dependencias por constructor
    public MermaService(MermaDAO mermaDAO, 
                        ProductoDAO productoDAO, 
                        KardexDAO kardexDAO) {
        this.mermaDAO = mermaDAO;
        this.productoDAO = productoDAO;
        this.kardexDAO = kardexDAO;
    }

    /**
     * Procesa la baja de un medicamento o producto por merma de forma transaccional.
     * Reemplaza por completo el bloque complejo de consultas que tenías en MermaDAO.
     */
    @Transactional(rollbackFor = Exception.class)
    public void procesarMerma(String nroSerie, String motivo, int idUsuario) throws Exception {
        
        // 1. Validar y obtener la serie en estado disponible
        Series serie = mermaDAO.findByNumeroSerieAndEstado(nroSerie, "DISPONIBLE")
                .orElseThrow(() -> new Exception("La serie '" + nroSerie + "' no está disponible o no existe en el inventario."));

        // 2. Cambiar estado de la serie a 'MERMA' y actualizar base de datos
        serie.setEstado("MERMA");
        mermaDAO.save(serie);

        // 3. Descontar el stock actual del producto asociado
        Producto producto = productoDAO.findById(serie.getIdProducto())
                .orElseThrow(() -> new Exception("El producto asociado a la serie ya no existe en el catálogo."));
        
        producto.setStockActual(producto.getStockActual() - 1);
        productoDAO.save(producto);

        // 4. Registrar la salida física por descarte en el historial del Kardex
        MovimientoKardex kardex = new MovimientoKardex();
        kardex.setIdProducto(producto.getIdProducto());
        kardex.setTipoMovimiento("MERMA");
        kardex.setCantidad(1);
        kardex.setMotivo("MERMA S/N: " + nroSerie + " | Motivo: " + motivo);
        kardex.setIdUsuario(idUsuario);
        kardex.setFecha(new Timestamp(System.currentTimeMillis()));
        
        kardexDAO.save(kardex);
    }
}
