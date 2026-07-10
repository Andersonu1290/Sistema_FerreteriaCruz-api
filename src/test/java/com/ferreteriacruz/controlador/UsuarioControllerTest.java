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

import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.servicio.ServicioUsuario;
import com.ferreteriacruz.config.JwtUtil; // 🔥 1. AÑADIMOS LA IMPORTACIÓN

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private ServicioUsuario servicioUsuario;

    @Mock
    private JwtUtil jwtUtil; // 🔥 2. DECLARAMOS EL MOCK DE JWTUTIL

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
    void registrarClienteDesdeTienda_setsRoleAndDelegates() {
        Usuario u = new Usuario();
        u.setUsername("c");
        u.setPassword("p");
        
        // Le decimos a los mocks qué deben responder para que la prueba pase
        when(servicioUsuario.registrarNuevoPersonal(any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token-falso-123"); // 🔥 3. SIMULAMOS LA CREACIÓN DEL TOKEN
        
        ResponseEntity<?> resp = usuarioController.registrarClienteDesdeTienda(u);
        
        assertEquals(201, resp.getStatusCode().value());
        assertEquals("CLIENTE", u.getRol());
    }
}