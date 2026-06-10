/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ferreteriacruz.servicio;

/**
 *
 * @author Anderson
 */

import java.util.List;
import ferreteriacruz.dao.ProductoDAOImpl;
import ferreteriacruz.dao.SeriesDAO;
import ferreteriacruz.modelo.Producto;
import ferreteriacruz.modelo.Series;

public class ServicioProducto implements IConsultaStock, IRegistroProducto {
    
    private ProductoDAOImpl pDao = new ProductoDAOImpl();

    @Override
    public List<Producto> obtenerInventarioActivo() {
        return pDao.listarProductos();
    }

    @Override
    public boolean registrarNuevoProducto(String sku, String nombre) {
        Producto p = new Producto();
        p.setCodigoSKU(sku);
        p.setNombre(nombre);
        p.setIdCategoria(1);
        p.setStockActual(0);
        p.setStockMinimo(5);
        p.setPrecio(0.0);
        return pDao.registrarProducto(p);
    }
    
    public boolean guardarProducto(Producto p) {
        boolean exito = false;
        int diferenciaStock = 0;
        boolean esNuevo = (p.getIdProducto() == 0);

        if (esNuevo) {
            exito = pDao.registrarProducto(p);
            diferenciaStock = p.getStockActual();
        } else {
            Producto prodAnterior = pDao.buscarPorId(p.getIdProducto());
            int stockAnterior = (prodAnterior != null) ? prodAnterior.getStockActual() : 0;
            exito = pDao.actualizarProducto(p);
            
            diferenciaStock = p.getStockActual() - stockAnterior;
        }

        if (exito && diferenciaStock != 0) {
            int idProd = p.getIdProducto();
            
            if (esNuevo) {
                Producto insertado = pDao.buscarPorSKU(p.getCodigoSKU()); 
                if(insertado != null) {
                    idProd = insertado.getIdProducto();
                }
            }
            
            if (diferenciaStock > 0) {
                generarSeriesHardware(idProd, p.getCodigoSKU(), diferenciaStock);
            } else if (diferenciaStock < 0) {
                removerSeriesHardware(idProd, Math.abs(diferenciaStock));
            }
        }
        
        return exito;
    }

    private void generarSeriesHardware(int idProducto, String sku, int cantidad) {
        SeriesDAO serieDao = new SeriesDAO();

        for (int i = 0; i < cantidad; i++) {
            Series serie = new Series();
            serie.setIdProducto(idProducto);
            
            String snUnico = sku + "-" + System.currentTimeMillis() + "-" + i;
            serie.setNumeroSerie(snUnico);
            serie.setEstado("DISPONIBLE"); 
            
            serieDao.registrarSerie(serie);
        }
    }

    private void removerSeriesHardware(int idProducto, int cantidad) {
        SeriesDAO serieDao = new SeriesDAO();
        serieDao.eliminarSeriesDisponibles(idProducto, cantidad);
    }
    
    public boolean eliminarProducto(int id) {
        return pDao.eliminarProducto(id);
    }
    
    public Producto buscarProducto(int id) {
        return pDao.buscarPorId(id);
    }
}