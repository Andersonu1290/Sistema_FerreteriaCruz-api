package com.ferreteriacruz.servicio;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class ServicioUsuarioTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ServicioUsuario servicioUsuario;

    @Test
    void testValidarAcceso_success() {
        Usuario u = new Usuario();
        u.setUsername("juan");
        u.setPassword("hash-pwd");

        when(usuarioRepository.findByUsername("juan")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pwd", "hash-pwd")).thenReturn(true);

        Usuario res = servicioUsuario.validarAcceso("juan", "pwd");
        assertNotNull(res);
        assertEquals("juan", res.getUsername());
    }

    @Test
    void testValidarAcceso_invalid_returnsNull() {
        when(usuarioRepository.findByUsername("x")).thenReturn(Optional.empty());
        assertNull(servicioUsuario.validarAcceso("x", "y"));
    }

    @Test
    void testRegistrarNuevoPersonal_existingUsername_returnsFalse() {
        Usuario u = new Usuario();
        u.setUsername("exists");
        u.setPassword("123456");   // <-- agregar
    
        when(usuarioRepository.existsByUsername("exists")).thenReturn(true);
    
        boolean ok = servicioUsuario.registrarNuevoPersonal(u);
    
        assertFalse(ok);
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

}

