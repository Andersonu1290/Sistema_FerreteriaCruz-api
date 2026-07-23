package com.ferreteriacruz.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteriacruz.config.JwtUtil; // 🔥 1. IMPORTANTE AÑADIR ESTO
import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.servicio.ServicioUsuario;
import com.ferreteriacruz.dto.RegistroClienteDTO;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final ServicioUsuario servicioUsuario;
    private final JwtUtil jwtUtil; // 🔥 2. DECLARAR EL UTILITARIO JWT

    // 🔥 3. INYECTARLO EN EL CONSTRUCTOR
    public UsuarioController(ServicioUsuario servicioUsuario, JwtUtil jwtUtil) {
        this.servicioUsuario = servicioUsuario;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarPersonal() {
        List<Usuario> usuarios = servicioUsuario.obtenerListaPersonal();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        // ... (Tu lógica para administradores se mantiene igual)
        try {
            if (nuevoUsuario.getUsername() == null || nuevoUsuario.getPassword() == null || nuevoUsuario.getRol() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Faltan datos requeridos: username, password o rol."));
            }

            boolean registrado = servicioUsuario.registrarNuevoPersonal(nuevoUsuario);

            if (registrado) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("mensaje", "Personal registrado de forma exitosa en el sistema."));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "El nombre de usuario '" + nuevoUsuario.getUsername() + "' ya se encuentra en uso."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ocurrió un error al procesar el registro: " + e.getMessage()));
        }
    }

    /**
     * Endpoint: POST /api/v1/usuarios/registro-tienda
     * Ahora devuelve el Token JWT para que el frontend haga Auto-Login.
     */
        
    @PostMapping("/registro-tienda")
    public ResponseEntity<?> registrarClienteDesdeTienda(@RequestBody RegistroClienteDTO dto) {
        try {
            if (dto.username() == null || dto.password() == null || dto.dni() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Faltan campos obligatorios para el registro."));
            }

            // Llamamos al nuevo servicio que guarda en ambas tablas
            Usuario nuevoUsuario = servicioUsuario.registrarClienteCompleto(dto);

            // Generamos el Token
            String token = jwtUtil.generateToken(nuevoUsuario.getUsername(), nuevoUsuario.getRol());
            nuevoUsuario.setPassword(null); // Limpiamos la clave por seguridad

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensaje", "Tu cuenta de cliente ha sido creada exitosamente.",
                            "token", token,
                            "usuario", nuevoUsuario
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al procesar el registro: " + e.getMessage()));
        }
    }
    @GetMapping("/perfil/{idUsuario}")
    public ResponseEntity<?> obtenerPerfilCliente(@PathVariable int idUsuario) {
        try {
            com.ferreteriacruz.modelo.UsuarioCliente perfil = servicioUsuario.obtenerPerfilCliente(idUsuario);
            if (perfil != null) {
                return ResponseEntity.ok(perfil);
            }
            // Si devuelve 204 No Content, el frontend sabrá que es un empleado o un usuario sin datos
            return ResponseEntity.noContent().build(); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al obtener perfil: " + e.getMessage()));
        }
    }
}