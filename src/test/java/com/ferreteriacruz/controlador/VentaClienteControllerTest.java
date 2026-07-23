package com.ferreteriacruz.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ferreteriacruz.dto.DetalleVentaClienteDTO;
import com.ferreteriacruz.dto.ItemCarritoDTO;
import com.ferreteriacruz.dto.PedidoClienteRequestDTO;
import com.ferreteriacruz.dto.PedidoClienteResponseDTO;
import com.ferreteriacruz.servicio.ServicioVentaCliente;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class VentaClienteControllerTest {

    @Mock
    private ServicioVentaCliente servicioVentaCliente;

    @InjectMocks
    private VentaClienteController ventaClienteController;

    @Test
    void crearPedido_success_returnsCreated() throws Exception {
        PedidoClienteRequestDTO request = crearRequest();
        PedidoClienteResponseDTO response = crearResponse();

        when(servicioVentaCliente.crearPedido(request)).thenReturn(response);

        ResponseEntity<?> result = ventaClienteController.crearPedido(request);

        assertEquals(201, result.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertEquals(true, body.get("success"));
        assertEquals("Pedido creado exitosamente", body.get("mensaje"));
        assertSame(response, body.get("pedido"));
    }

    @Test
    void obtenerPedido_success_returnsOk() throws Exception {
        PedidoClienteResponseDTO response = crearResponse();

        when(servicioVentaCliente.obtenerPedidoPorId(15)).thenReturn(response);

        ResponseEntity<?> result = ventaClienteController.obtenerPedido(15);

        assertEquals(200, result.getStatusCode().value());
        assertSame(response, result.getBody());
    }

    @Test
    void obtenerPedidosCliente_success_returnsCollection() {
        PedidoClienteResponseDTO response = crearResponse();

        when(servicioVentaCliente.obtenerPedidosCliente(7)).thenReturn(List.of(response));

        ResponseEntity<?> result = ventaClienteController.obtenerPedidosCliente(7);

        assertEquals(200, result.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertEquals(true, body.get("success"));
        assertEquals(1, body.get("total"));
        assertEquals(1, ((List<?>) body.get("pedidos")).size());
    }

    @Test
    void cancelarPedido_success_returnsOk() throws Exception {
        ResponseEntity<?> result = ventaClienteController.cancelarPedido(18);

        assertEquals(200, result.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) result.getBody();
        assertEquals(true, body.get("success"));
        assertEquals("Pedido cancelado correctamente", body.get("mensaje"));
    }

    private PedidoClienteRequestDTO crearRequest() {
        return new PedidoClienteRequestDTO(
                7,
                "12345678",
                "Ana",
                "Perez",
                "ana@example.com",
                "999999999",
                "Av. Principal 123",
                "123",
                "Dpto 2",
                "Lima",
                "Lima",
                "15001",
                "NORMAL",
                5.0,
                "YAPE",
                null,
                null,
                null,
                null,
                List.of(new ItemCarritoDTO(1, 2, 15.0)),
                "Entregar en horario de oficina"
        );
    }

    private PedidoClienteResponseDTO crearResponse() {
        PedidoClienteResponseDTO response = new PedidoClienteResponseDTO();
        response.setIdVentaCliente(15);
        response.setNroPedido("PED-2026-00015");
        response.setEstado("PENDIENTE");
        response.setFechaPedido(LocalDateTime.of(2026, 7, 4, 10, 30));
        response.setSubtotal(30.0);
        response.setCostoEnvio(5.0);
        response.setTotal(35.0);
        response.setNombreCliente("Ana Perez");
        response.setDniCliente("12345678");
        response.setEmailCliente("ana@example.com");
        response.setTelefonoCliente("999999999");
        response.setDireccionEnvio("Av. Principal 123");
        response.setCiudad("Lima");
        response.setDepartamento("Lima");
        response.setTipoEnvio("NORMAL");
        response.setTipoPago("YAPE");
        
        response.setDetalles(List.of(
                new DetalleVentaClienteDTO(1, 1, "Gel Antibacterial", 2, 15.0, 30.0, 0.0)
        ));
        return response;
    }
}