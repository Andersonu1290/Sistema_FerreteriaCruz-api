package com.ferreteriacruz.servicio;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

import com.ferreteriacruz.dto.DetalleVentaClienteDTO;
import com.ferreteriacruz.dto.ItemCarritoDTO;
import com.ferreteriacruz.dto.PedidoClienteRequestDTO;
import com.ferreteriacruz.dto.PedidoClienteResponseDTO;
import com.ferreteriacruz.modelo.DetalleVentaCliente;
import com.ferreteriacruz.modelo.MovimientoKardex;
import com.ferreteriacruz.modelo.Producto;
import com.ferreteriacruz.modelo.VentaCliente;
import com.ferreteriacruz.patrones.observer.GestorStock;
import com.ferreteriacruz.repository.DetalleVentaClienteRepository;
import com.ferreteriacruz.repository.KardexRepository;
import com.ferreteriacruz.repository.ProductoRepository;
import com.ferreteriacruz.repository.VentaClienteRepository;
import com.ferreteriacruz.repository.SeriesRepository;
import com.google.common.base.Preconditions;

@Service
public class ServicioVentaCliente {

    // Logback (vía SLF4J) - Registra toda la actividad del Ecommerce
    private static final Logger log = LoggerFactory.getLogger(ServicioVentaCliente.class);

    private final VentaClienteRepository ventaClienteRepository;
    private final DetalleVentaClienteRepository detalleVentaClienteRepository;
    private final ProductoRepository productoRepository;
    private final KardexRepository kardexRepository;
    private final GestorStock gestorStock;
    private final SeriesRepository seriesRepository;

    public ServicioVentaCliente(VentaClienteRepository ventaClienteRepository,
                                 DetalleVentaClienteRepository detalleVentaClienteRepository,
                                 ProductoRepository productoRepository,
                                 KardexRepository kardexRepository,
                                 GestorStock gestorStock,
                                 SeriesRepository seriesRepository) {
        this.ventaClienteRepository = ventaClienteRepository;
        this.detalleVentaClienteRepository = detalleVentaClienteRepository;
        this.productoRepository = productoRepository;
        this.kardexRepository = kardexRepository;
        this.gestorStock = gestorStock;
        this.seriesRepository = seriesRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public PedidoClienteResponseDTO crearPedido(PedidoClienteRequestDTO request) throws Exception {
        try {
            // Guava: Validaciones defensivas bloquean payloads corruptos de inmediato
            Preconditions.checkNotNull(request, "El pedido no puede ser nulo");
            Preconditions.checkArgument(request.items() != null && !request.items().isEmpty(), "El pedido debe contener al menos un producto");
            Preconditions.checkArgument(request.idUsuario() > 0, "ID de usuario inválido");

            log.info("Procesando nuevo pedido e-commerce para el usuario ID: {}", request.idUsuario());

            double subtotal = 0.0;
            for (ItemCarritoDTO item : request.items()) {
                Preconditions.checkArgument(item.cantidad() > 0, "La cantidad debe ser mayor a cero");
                subtotal += item.precioUnitario() * item.cantidad();
            }

            double costoEnvio = request.costoEnvio() != 0 ? request.costoEnvio() : 0.0;
            double total = subtotal + costoEnvio;

            String nroPedido = generarNumeroPedido();

            // Apache Commons Lang3: Limpieza de textos basura ingresados por el cliente
            String nombreLimpio = StringUtils.normalizeSpace(request.nombreCliente());
            String emailLimpio = StringUtils.trim(request.emailCliente());
            String dniLimpio = StringUtils.trim(request.dniCliente());

            VentaCliente ventaCliente = new VentaCliente(
                    request.idUsuario(), nroPedido, "PENDIENTE", LocalDateTime.now(),
                    subtotal, costoEnvio, total, dniLimpio, nombreLimpio,
                    emailLimpio, StringUtils.trim(request.telefonoCliente()), request.direccionEnvio(),
                    request.ciudad(), request.departamento(), request.tipoEnvio(), request.tipoPago()
            );

            ventaCliente.setApellidoCliente(StringUtils.normalizeSpace(request.apellidoCliente()));
            ventaCliente.setNumeroCalle(request.numeroCalle());
            ventaCliente.setApartamento(request.apartamento());
            ventaCliente.setCodigoPostal(request.codigoPostal());
            ventaCliente.setTipoTarjeta(request.tipoTarjeta());
            ventaCliente.setUltimos4Digitos(request.ultimos4Digitos());
            ventaCliente.setBancoTarjeta(request.bancoTarjeta());
            ventaCliente.setNombreTitular(StringUtils.normalizeSpace(request.nombreTitular()));
            ventaCliente.setObservaciones(StringUtils.normalizeSpace(request.observaciones()));
            ventaCliente.setFechaEntregaEstimada(calcularFechaEntrega(request.tipoEnvio()));

            VentaCliente ventaGuardada = ventaClienteRepository.save(ventaCliente);
            log.debug("Cabecera del pedido guardada: {}", nroPedido);

            List<DetalleVentaClienteDTO> detallesDTO = new ArrayList<>();
            for (ItemCarritoDTO item : request.items()) {
                DetalleVentaCliente detalle = new DetalleVentaCliente(
                        ventaGuardada.getIdVentaCliente(), item.idProducto(), item.cantidad(),
                        item.precioUnitario(), item.precioUnitario() * item.cantidad()
                );
                DetalleVentaCliente detalleGuardado = detalleVentaClienteRepository.save(detalle);

                Producto producto = productoRepository.findById(item.idProducto())
                        .orElseThrow(() -> new Exception("Producto no encontrado en BD"));

                if (producto.getStockActual() < item.cantidad()) {
                    log.error("Stock insuficiente en backend para el producto: {}", producto.getNombre());
                    throw new Exception("Stock insuficiente en el backend para: " + producto.getNombre());
                }

                producto.setStockActual(producto.getStockActual() - item.cantidad());
                productoRepository.save(producto);

                List<com.ferreteriacruz.modelo.Series> seriesDisponibles = seriesRepository.findByIdProductoAndEstado(producto.getIdProducto(), "DISPONIBLE");
                
                if (seriesDisponibles.size() < item.cantidad()) {
                    log.error("Desfase físico. Faltan cajas disponibles para: {}", producto.getNombre());
                    throw new Exception("Desfase de stock físico: No hay suficientes series DISPONIBLES para " + producto.getNombre());
                }

                List<com.ferreteriacruz.modelo.Series> seriesAAsignar = new ArrayList<>();
                for (int i = 0; i < item.cantidad(); i++) {
                    com.ferreteriacruz.modelo.Series s = seriesDisponibles.get(i);
                    s.setEstado("ASIGNADO"); 
                    seriesAAsignar.add(s);
                }
                seriesRepository.saveAll(seriesAAsignar); 

                MovimientoKardex kardex = new MovimientoKardex();
                kardex.setIdProducto(producto.getIdProducto());
                kardex.setTipoMovimiento("SALIDA");
                kardex.setCantidad(item.cantidad());
                kardex.setMotivo("Pedido Web: " + ventaGuardada.getNroPedido());
                kardex.setIdUsuario(request.idUsuario());
                kardex.setFecha(new Timestamp(System.currentTimeMillis()));
                kardexRepository.save(kardex);

                if (producto.getStockActual() <= producto.getStockMinimo()) {
                    log.warn("Alerta: El stock del producto {} bajó al límite", producto.getCodigoSKU());
                    gestorStock.dispararAlertaStockCritico(producto.getCodigoSKU(), producto.getStockActual());
                }

                detallesDTO.add(new DetalleVentaClienteDTO(
                        detalleGuardado.getIdDetalle(), item.idProducto(), producto.getNombre(),
                        item.cantidad(), item.precioUnitario(), detalleGuardado.getSubtotal(), 0.0
                ));
            }

            PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                    ventaGuardada.getIdVentaCliente(), ventaGuardada.getNroPedido(), ventaGuardada.getEstado(),
                    ventaGuardada.getFechaPedido(), ventaGuardada.getFechaEntregaEstimada(), ventaGuardada.getSubtotal(),
                    ventaGuardada.getCostoEnvio(), ventaGuardada.getTotal(), ventaGuardada.getNombreCliente(),
                    ventaGuardada.getEmailCliente(), ventaGuardada.getTelefonoCliente(), ventaGuardada.getDireccionEnvio(),
                    ventaGuardada.getCiudad(), ventaGuardada.getDepartamento(), ventaGuardada.getTipoEnvio(),
                    ventaGuardada.getNumeroSeguimiento(), ventaGuardada.getTipoPago(), ventaGuardada.getTipoTarjeta(),
                    ventaGuardada.getUltimos4Digitos()
            );
            response.setDetalles(detallesDTO);

            log.info("Pedido {} completado exitosamente", nroPedido);
            return response;

        } catch (Exception e) {
            log.error("Fallo al crear el pedido: {}", e.getMessage());
            throw new Exception("Error al crear el pedido: " + e.getMessage());
        }
    }

    public PedidoClienteResponseDTO obtenerPedidoPorId(int idVentaCliente) throws Exception {
        VentaCliente venta = ventaClienteRepository.findById(idVentaCliente)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));

        List<DetalleVentaCliente> detalles = detalleVentaClienteRepository.findByIdVentaCliente(idVentaCliente);

        List<DetalleVentaClienteDTO> detallesDTO = detalles.stream().map(d -> {
            Optional<Producto> producto = productoRepository.findById(d.getIdProducto());
            String nombreProducto = producto.map(Producto::getNombre).orElse("Producto");
            return new DetalleVentaClienteDTO(
                    d.getIdDetalle(), d.getIdProducto(), nombreProducto, d.getCantidad(),
                    d.getPrecioUnitario(), d.getSubtotal(), d.getDescuento()
            );
        }).collect(Collectors.toList());

        PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                venta.getIdVentaCliente(), venta.getNroPedido(), venta.getEstado(),
                venta.getFechaPedido(), venta.getFechaEntregaEstimada(), venta.getSubtotal(),
                venta.getCostoEnvio(), venta.getTotal(), venta.getNombreCliente(),
                venta.getEmailCliente(), venta.getTelefonoCliente(), venta.getDireccionEnvio(),
                venta.getCiudad(), venta.getDepartamento(), venta.getTipoEnvio(),
                venta.getNumeroSeguimiento(), venta.getTipoPago(), venta.getTipoTarjeta(),
                venta.getUltimos4Digitos()
        );
        response.setDetalles(detallesDTO);
        return response;
    }

    public List<PedidoClienteResponseDTO> obtenerPedidosCliente(int idUsuario) {
        List<VentaCliente> ventas = ventaClienteRepository.findByIdUsuario(idUsuario);

        // 🔥 OPTIMIZACIÓN: Traemos los productos a la RAM una sola vez
        Map<Integer, String> mapaProductos = productoRepository.findAll().stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre));

        return ventas.stream().map(venta -> {
            // Trae los detalles del pedido específico
            List<DetalleVentaCliente> detalles = detalleVentaClienteRepository.findByIdVentaCliente(venta.getIdVentaCliente());

            List<DetalleVentaClienteDTO> detallesDTO = detalles.stream().map(d -> {
                // 🔥 OPTIMIZACIÓN: Ya no consulta a la BD por cada ítem, lo busca instantáneamente en el mapa
                String nombreProducto = mapaProductos.getOrDefault(d.getIdProducto(), "Producto");
                return new DetalleVentaClienteDTO(
                        d.getIdDetalle(), d.getIdProducto(), nombreProducto, d.getCantidad(),
                        d.getPrecioUnitario(), d.getSubtotal(), d.getDescuento()
                );
            }).collect(Collectors.toList());

            PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                    venta.getIdVentaCliente(), venta.getNroPedido(), venta.getEstado(),
                    venta.getFechaPedido(), venta.getFechaEntregaEstimada(), venta.getSubtotal(),
                    venta.getCostoEnvio(), venta.getTotal(), venta.getNombreCliente(),
                    venta.getEmailCliente(), venta.getTelefonoCliente(), venta.getDireccionEnvio(),
                    venta.getCiudad(), venta.getDepartamento(), venta.getTipoEnvio(),
                    venta.getNumeroSeguimiento(), venta.getTipoPago(), venta.getTipoTarjeta(),
                    venta.getUltimos4Digitos()
            );
            response.setDetalles(detallesDTO);
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public PedidoClienteResponseDTO actualizarEstadoPedido(int idVentaCliente, String nuevoEstado, String numeroSeguimiento) throws Exception {
        VentaCliente venta = ventaClienteRepository.findById(idVentaCliente)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));
        
        log.info("Actualizando estado de pedido {}: {} -> {}", venta.getNroPedido(), venta.getEstado(), nuevoEstado);

        venta.setEstado(StringUtils.trim(nuevoEstado));
        if (StringUtils.isNotBlank(numeroSeguimiento)) {
            venta.setNumeroSeguimiento(StringUtils.trim(numeroSeguimiento));
        }

        if ("ENTREGADO".equalsIgnoreCase(nuevoEstado)) {
            venta.setFechaEntregaReal(LocalDateTime.now());
        }

        VentaCliente ventaActualizada = ventaClienteRepository.save(venta);
        return obtenerPedidoPorId(ventaActualizada.getIdVentaCliente());
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelarPedido(int idVentaCliente) throws Exception {
        VentaCliente venta = ventaClienteRepository.findById(idVentaCliente)
                .orElseThrow(() -> new Exception("Pedido no encontrado"));

        if ("ENVIADO".equalsIgnoreCase(venta.getEstado()) || "ENTREGADO".equalsIgnoreCase(venta.getEstado())) {
            log.warn("Intento de cancelar pedido que ya fue despachado: {}", venta.getNroPedido());
            throw new Exception("No se puede cancelar un pedido ya enviado o entregado");
        }
        
        log.info("Cancelando pedido Web {} y devolviendo stock físico", venta.getNroPedido());

        List<DetalleVentaCliente> detalles = detalleVentaClienteRepository.findByIdVentaCliente(idVentaCliente);
        for (DetalleVentaCliente detalle : detalles) {
            Producto producto = productoRepository.findById(detalle.getIdProducto()).orElse(null);
            if (producto != null) {
                
                producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
                productoRepository.save(producto);

                List<com.ferreteriacruz.modelo.Series> seriesAsignadas = seriesRepository.findByIdProductoAndEstado(producto.getIdProducto(), "ASIGNADO");
                List<com.ferreteriacruz.modelo.Series> seriesALiberar = new ArrayList<>();
                for (int i = 0; i < detalle.getCantidad() && i < seriesAsignadas.size(); i++) {
                    com.ferreteriacruz.modelo.Series s = seriesAsignadas.get(i);
                    s.setEstado("DISPONIBLE"); 
                    seriesALiberar.add(s);
                }
                seriesRepository.saveAll(seriesALiberar);

                MovimientoKardex kardex = new MovimientoKardex();
                kardex.setIdProducto(producto.getIdProducto());
                kardex.setTipoMovimiento("INGRESO");
                kardex.setCantidad(detalle.getCantidad());
                kardex.setMotivo("Cancelación Pedido Web: " + venta.getNroPedido());
                kardex.setIdUsuario(venta.getIdUsuario());
                kardex.setFecha(new Timestamp(System.currentTimeMillis()));
                kardexRepository.save(kardex);
            }
        }

        venta.setEstado("CANCELADO");
        ventaClienteRepository.save(venta);
    }

    public List<PedidoClienteResponseDTO> obtenerPedidosPorEstado(String estado) {
        List<VentaCliente> ventas = ventaClienteRepository.findByEstado(StringUtils.trim(estado));
        return ventas.stream().map(venta -> {
            try {
                return obtenerPedidoPorId(venta.getIdVentaCliente());
            } catch (Exception e) {
                return null;
            }
        }).filter(p -> p != null).collect(Collectors.toList());
    }

    private String generarNumeroPedido() {
        long numero = ventaClienteRepository.countTotalVentas() + 1;
        return String.format("PED-%tY-%05d", LocalDateTime.now(), numero);
    }

    private LocalDate calcularFechaEntrega(String tipoEnvio) {
        LocalDate today = LocalDate.now();
        switch (StringUtils.trimToEmpty(tipoEnvio).toUpperCase()) {
            case "EXPRESS":
                return today.plusDays(2); 
            case "SAME_DAY":
                return today.plusDays(1); 
            default: 
                return today.plusDays(5); 
        }
    }

    /**
     * OBTENER TODOS LOS PEDIDOS (OPTIMIZADO PARA EVITAR EL PROBLEMA N+1)
     */
    public List<PedidoClienteResponseDTO> obtenerTodosLosPedidos() {
        // 1. Traemos cabeceras ordenadas (Consulta 1)
        List<VentaCliente> ventas = ventaClienteRepository.findAll();
        ventas.sort((a, b) -> b.getFechaPedido().compareTo(a.getFechaPedido()));

        // 2. Traemos TODOS los productos a la RAM para acceso instantáneo (Consulta 2)
        Map<Integer, String> mapaProductos = productoRepository.findAll().stream()
                .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre));

        // 3. Traemos TODOS los detalles y los agrupamos por ID de Venta (Consulta 3)
        Map<Integer, List<DetalleVentaCliente>> mapaDetalles = detalleVentaClienteRepository.findAll().stream()
                .collect(Collectors.groupingBy(DetalleVentaCliente::getIdVentaCliente));

        // 4. Ensamblamos todo en memoria (Sin tocar la BD en el bucle)
        return ventas.stream().map(venta -> {
            
            // Obtenemos los detalles de esta venta específica desde nuestro mapa en RAM
            List<DetalleVentaCliente> detallesVenta = mapaDetalles.getOrDefault(venta.getIdVentaCliente(), new ArrayList<>());

            List<DetalleVentaClienteDTO> detallesDTO = detallesVenta.stream().map(d -> {
                // Buscamos el nombre del producto en nuestro mapa en RAM
                String nombreProducto = mapaProductos.getOrDefault(d.getIdProducto(), "Producto Desconocido");
                
                return new DetalleVentaClienteDTO(
                        d.getIdDetalle(), d.getIdProducto(), nombreProducto, d.getCantidad(),
                        d.getPrecioUnitario(), d.getSubtotal(), d.getDescuento()
                );
            }).collect(Collectors.toList());

            PedidoClienteResponseDTO response = new PedidoClienteResponseDTO(
                    venta.getIdVentaCliente(), venta.getNroPedido(), venta.getEstado(),
                    venta.getFechaPedido(), venta.getFechaEntregaEstimada(), venta.getSubtotal(),
                    venta.getCostoEnvio(), venta.getTotal(), venta.getNombreCliente(),
                    venta.getEmailCliente(), venta.getTelefonoCliente(), venta.getDireccionEnvio(),
                    venta.getCiudad(), venta.getDepartamento(), venta.getTipoEnvio(),
                    venta.getNumeroSeguimiento(), venta.getTipoPago(), venta.getTipoTarjeta(),
                    venta.getUltimos4Digitos()
            );
            response.setDetalles(detallesDTO);
            return response;
            
        }).collect(Collectors.toList());
    }
}