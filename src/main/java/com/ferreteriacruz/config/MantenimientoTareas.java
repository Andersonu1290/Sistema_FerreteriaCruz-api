package com.ferreteriacruz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MantenimientoTareas {

    private static final Logger log = LoggerFactory.getLogger(MantenimientoTareas.class);
    private final JdbcTemplate jdbcTemplate;

    public MantenimientoTareas(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void ejecutarMantenimientoNocturno() {
        log.info("Iniciando Cron Job de Mantenimiento Preventivo...");

        try {

            log.info("Ejecutando script de limpieza de carritos abandonados...");
            jdbcTemplate.update("DELETE FROM carrito_compras WHERE id_usuario NOT IN (SELECT id_usuario FROM usuarios)");

            log.info("Ejecutando script de optimización de tablas MySQL...");
            jdbcTemplate.execute("ANALYZE TABLE productos, ventas, venta_cliente, kardex_movimientos;");

            log.info("Mantenimiento preventivo finalizado con éxito.");

        } catch (Exception e) {
            log.error("Error crítico al ejecutar los scripts de mantenimiento nocturno", e);
        }
    }
}