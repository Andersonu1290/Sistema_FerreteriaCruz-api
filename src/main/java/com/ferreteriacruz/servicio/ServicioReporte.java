package com.ferreteriacruz.servicio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.dao.CategoriaDAO;
import com.ferreteriacruz.dao.ProductoDAO;
import com.ferreteriacruz.dao.SeriesDAO;
import com.ferreteriacruz.dao.VentaDAO;
import com.ferreteriacruz.dao.VentaClienteDAO;
import com.ferreteriacruz.modelo.Venta;
import com.ferreteriacruz.modelo.Categoria;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Service
public class ServicioReporte implements IGeneraReporte {

    // Logback (vía SLF4J) - Issue #11
    private static final Logger log = LoggerFactory.getLogger(ServicioReporte.class);

    private final ProductoDAO productoRepository;
    private final VentaDAO ventaRepository;
    private final VentaClienteDAO ventaClienteRepository; 
    private final SeriesDAO seriesRepository;
    private final CategoriaDAO categoriaRepository;

    public ServicioReporte(ProductoDAO productoRepository, 
                           VentaDAO ventaRepository, 
                           VentaClienteDAO ventaClienteRepository,
                           SeriesDAO seriesRepository,
                           CategoriaDAO categoriaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.ventaClienteRepository = ventaClienteRepository;
        this.seriesRepository = seriesRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Map<String, Integer> generarResumenEjecutivo() {
        Map<String, Integer> kpis = new HashMap<>();

        kpis.put("totalStock", productoRepository.obtenerTotalUnidadesStock());
        
        int ventasPos = ventaRepository.contarVentasCompletadas();
        int ventasWeb = ventaClienteRepository.contarVentasCompletadasWeb();
        kpis.put("totalVentas", ventasPos + ventasWeb);

        kpis.put("totalMermas", (int) seriesRepository.countByEstado("MERMA"));
        kpis.put("stockCritico", productoRepository.contarStockCritico());

        return kpis;
    }

    public double obtenerIngresosTotales() { 
        double ingresosPos = ventaRepository.obtenerTotalIngresos();
        double ingresosWeb = ventaClienteRepository.obtenerTotalIngresosWeb();
        return ingresosPos + ingresosWeb;
    }

    public String[] obtenerTopProductos() { 
        List<Object[]> topPos = ventaRepository.obtenerTopProductosConCategoria(PageRequest.of(0, 100));
        List<Object[]> topWeb = ventaClienteRepository.obtenerTopProductosWeb();
        
        Map<String, ProductoKpi> consolidado = new HashMap<>();

        for (Object[] fila : topPos) {
            String prod = (String) fila[0];
            int cant = ((Number) fila[1]).intValue();
            String cat = fila[2] != null ? (String) fila[2] : "Sin Categoría";
            consolidado.put(prod, new ProductoKpi(prod, cant, cat));
        }

        for (Object[] fila : topWeb) {
            String prod = (String) fila[0];
            int cant = ((Number) fila[1]).intValue();
            String cat = fila[2] != null ? (String) fila[2] : "Sin Categoría";
            
            if (consolidado.containsKey(prod)) {
                consolidado.get(prod).cantidad += cant;
            } else {
                consolidado.put(prod, new ProductoKpi(prod, cant, cat));
            }
        }

        return consolidado.values().stream()
                .sorted((a, b) -> Integer.compare(b.cantidad, a.cantidad))
                .limit(5)
                .map(k -> k.nombre + "||" + k.cantidad + "||" + k.categoria)
                .toArray(String[]::new);
    }

    public String[] obtenerStockCategoria() { 
        List<Object[]> resultados = productoRepository.obtenerStockPorCategoria();
        String[] stockPorCategoria = new String[resultados.size()];
        for (int i = 0; i < resultados.size(); i++) {
            Object[] fila = resultados.get(i);
            String categoria = (String) fila[0];
            Number stock = fila[1] != null ? (Number) fila[1] : 0; 
            stockPorCategoria[i] = categoria + "||" + stock;
        }
        return stockPorCategoria; 
    }

    // 🔥 NUEVO MÉTODO: UNIFICA Y ORDENA LAS ÚLTIMAS 5 VENTAS (CAJA + WEB)
    public List<Venta> obtenerAuditoriaGlobal() {
        // 1. Obtener ventas físicas (POS)
        List<Venta> ventasPos = ventaRepository.listarVentasConNombres().stream().map(r -> {
            Venta v = new Venta();
            v.setNroComprobante((String) r[5]);
            v.setNombreCliente((String) r[10]);
            v.setNombreProducto((String) r[11]);
            v.setTotal(((Number) r[7]).doubleValue());
            v.setEstado((String) r[9]);
            
            Object f = r[8];
            if (f instanceof java.time.LocalDateTime localDateTime) {
                v.setFecha(java.sql.Timestamp.valueOf(localDateTime));
            } else if (f instanceof java.sql.Timestamp timestamp) {
                v.setFecha((java.sql.Timestamp) timestamp);
            } else {
                v.setFecha((java.util.Date) f);
            }
            return v;
        }).collect(Collectors.toList());

        // 2. Obtener ventas Web (E-commerce)
        List<Venta> ventasWeb = ventaClienteRepository.findAll().stream().map(vc -> {
            Venta v = new Venta();
            v.setNroComprobante(vc.getNroPedido());
            v.setNombreCliente(vc.getNombreCliente());
            v.setNombreProducto("Varios (Web)"); // El E-commerce tiene múltiples productos, lo resumimos
            v.setTotal(vc.getTotal());
            v.setEstado(vc.getEstado());
            v.setFecha(java.sql.Timestamp.valueOf(vc.getFechaPedido()));
            return v;
        }).collect(Collectors.toList());

        // 3. Unir, ordenar por fecha descendente y tomar las últimas 5
        List<Venta> consolidadas = new ArrayList<>();
        consolidadas.addAll(ventasPos);
        consolidadas.addAll(ventasWeb);

        consolidadas.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        return consolidadas.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Genera un reporte de inventario en formato Excel (.xlsx) usando Apache POI.
     * Agrupa los productos por categoría usando un Multimap de Guava.
     * (Issue #11: uso de Apache POI + Google Guava)
     */
    public byte[] generarReporteInventarioExcel() throws IOException {
        log.info("Generando reporte de inventario en Excel");

        List<Producto> productos = productoRepository.findAll();
        Map<Integer, String> nombresCategoria = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(Categoria::getIdCategoria, Categoria::getNombre));

        Multimap<String, Producto> porCategoria = ArrayListMultimap.create();
        for (Producto p : productos) {
            String categoria = nombresCategoria.getOrDefault(p.getIdCategoria(), "Sin Categoria");
            porCategoria.put(categoria, p);
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventario Ferreteria Cruz");

            CellStyle estiloTitulo = workbook.createCellStyle();
            Font fuenteTitulo = workbook.createFont();
            fuenteTitulo.setBold(true);
            estiloTitulo.setFont(fuenteTitulo);

            int filaActual = 0;
            for (String categoria : porCategoria.keySet()) {
                Row filaCategoria = sheet.createRow(filaActual++);
                Cell celdaCategoria = filaCategoria.createCell(0);
                celdaCategoria.setCellValue(categoria);
                celdaCategoria.setCellStyle(estiloTitulo);

                Row encabezado = sheet.createRow(filaActual++);
                String[] columnas = {"SKU", "Nombre", "Stock Actual", "Stock Minimo", "Precio Unit."};
                for (int i = 0; i < columnas.length; i++) {
                    Cell c = encabezado.createCell(i);
                    c.setCellValue(columnas[i]);
                    c.setCellStyle(estiloTitulo);
                }

                for (Producto p : porCategoria.get(categoria)) {
                    Row fila = sheet.createRow(filaActual++);
                    fila.createCell(0).setCellValue(p.getCodigoSKU());
                    fila.createCell(1).setCellValue(p.getNombre());
                    fila.createCell(2).setCellValue(p.getStockActual());
                    fila.createCell(3).setCellValue(p.getStockMinimo());
                    fila.createCell(4).setCellValue(p.getPrecio());
                }
                filaActual++;
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            log.debug("Reporte Excel generado con {} productos en {} categorias", productos.size(), porCategoria.keySet().size());
            return out.toByteArray();
        }
    }

    // Clase auxiliar
    class ProductoKpi {
        String nombre; int cantidad; String categoria;
        public ProductoKpi(String nombre, int cantidad, String categoria) {
            this.nombre = nombre; this.cantidad = cantidad; this.categoria = categoria;
        }
    }
}