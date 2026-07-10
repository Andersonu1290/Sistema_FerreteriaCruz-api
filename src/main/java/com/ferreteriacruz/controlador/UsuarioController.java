package com.ferreteriacruz.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteriacruz.config.JwtUtil; // 🔥 1. IMPORTANTE AÑADIR ESTO
import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.servicio.ServicioUsuario;

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
    public ResponseEntity<?> registrarClienteDesdeTienda(@RequestBody Usuario nuevoUsuario) {
        try {
            if (nuevoUsuario.getUsername() == null || nuevoUsuario.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El usuario y la contraseña son obligatorios."));
            }

            nuevoUsuario.setRol("CLIENTE");

            boolean registrado = servicioUsuario.registrarNuevoPersonal(nuevoUsuario);

            if (registrado) {
                // 🔥 4. GENERAR TOKEN AUTOMÁTICAMENTE
                String token = jwtUtil.generateToken(nuevoUsuario.getUsername(), nuevoUsuario.getRol());
                
                // Limpiar la contraseña por seguridad antes de mandar el JSON
                nuevoUsuario.setPassword(null);

                // 🔥 5. DEVOLVER EL TOKEN Y EL USUARIO IGUAL QUE EN EL LOGIN
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of(
                                "mensaje", "Tu cuenta de cliente ha sido creada exitosamente.",
                                "token", token,
                                "usuario", nuevoUsuario
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "El nombre de usuario '" + nuevoUsuario.getUsername() + "' ya existe."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al procesar el registro: " + e.getMessage()));
        }
    }
}