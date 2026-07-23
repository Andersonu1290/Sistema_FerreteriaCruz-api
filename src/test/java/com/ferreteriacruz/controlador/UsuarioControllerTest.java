package com.ferreteriacruz.controlador;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.ferreteriacruz.config.JwtUtil;
import com.ferreteriacruz.dto.RegistroClienteDTO;
import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.servicio.ServicioUsuario;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private ServicioUsuario servicioUsuario;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void listarPersonal_returnsList() {
        when(servicioUsuario.obtenerListaPersonal()).thenReturn(List.of(new Usuario()));
        ResponseEntity<List<Usuario>> resp = usuarioController.listarPersonal();
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1, resp.getBody().size());
    }

    @Test
    void registrarUsuario_missingFields_returnsBadRequest() {
        Usuario u = new Usuario();
        ResponseEntity<?> resp = usuarioController.registrarUsuario(u);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void registrarClienteDesdeTienda_setsRoleAndDelegates() throws Exception {
        // Creamos el DTO tal como lo exige ahora el controlador
        RegistroClienteDTO dto = new RegistroClienteDTO("c", "p", "70123456", "Juan", "Pérez", "correo@ejemplo.com");
        
        Usuario uMock = new Usuario();
        uMock.setIdUsuario(1);
        uMock.setUsername("c");
        uMock.setRol("CLIENTE");
        
        // Simulamos la respuesta del servicio actualizado
        when(servicioUsuario.registrarClienteCompleto(any(RegistroClienteDTO.class))).thenReturn(uMock);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token-falso-123");
        
        ResponseEntity<?> resp = usuarioController.registrarClienteDesdeTienda(dto);
        
        assertEquals(201, resp.getStatusCode().value());
    }
}