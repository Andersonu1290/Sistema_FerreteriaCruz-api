package com.ferreteriacruz.controlador;

import com.ferreteriacruz.dto.ReporteDashboardDTO;
import com.ferreteriacruz.modelo.Venta;
import com.ferreteriacruz.servicio.ServicioReporte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reportes")
@CrossOrigin(origins = "*") 
public class ReporteController {

    // Logback (via SLF4J) - Issue #11
    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    private final ServicioReporte servicioReporte;

    public ReporteController(ServicioReporte servicioReporte) {
        this.servicioReporte = servicioReporte;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ReporteDashboardDTO> obtenerDashboardCompleto() {
        Map<String, Integer> kpis = servicioReporte.generarResumenEjecutivo();
        double ingresos = servicioReporte.obtenerIngresosTotales();
        String[] topProd = servicioReporte.obtenerTopProductos();
        String[] catStock = servicioReporte.obtenerStockCategoria();

        // 🔥 NUEVO: Traemos la auditoría fusionada de Tienda Física + Web
        List<Venta> ultimasVentas = servicioReporte.obtenerAuditoriaGlobal();

        ReporteDashboardDTO dashboard = new ReporteDashboardDTO(kpis, ingresos, topProd, catStock, ultimasVentas);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Endpoint: GET /api/v1/reportes/inventario/excel
     * Descarga el inventario completo en un archivo .xlsx generado con Apache POI.
     * (Issue #11)
     */
    @GetMapping("/inventario/excel")
    public ResponseEntity<?> exportarInventarioExcel() {
        try {
            byte[] archivo = servicioReporte.generarReporteInventarioExcel();
            log.info("Exportacion de inventario a Excel solicitada, {} bytes generados", archivo.length);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventario_ferreteriacruz.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(archivo);
        } catch (IOException e) {
            log.error("Error generando el reporte de inventario en Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo generar el reporte de inventario."));
        }
    }
}