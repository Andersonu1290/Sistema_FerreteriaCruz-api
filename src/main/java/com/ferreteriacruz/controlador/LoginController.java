package com.ferreteriacruz.controlador;

import com.ferreteriacruz.dto.LoginRequestDTO;
import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.servicio.ServicioUsuario;
import com.ferreteriacruz.config.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // Permite la comunicación segura con tu frontend
public class LoginController {

    private final ServicioUsuario servicioUsuario;
    private final JwtUtil jwtUtil;

    // Inyección de dependencias por constructor
    public LoginController(ServicioUsuario servicioUsuario, JwtUtil jwtUtil) {
        this.servicioUsuario = servicioUsuario;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint: POST /api/v1/auth/login
     * Reemplaza la sección "ingresar" de tu antiguo doPost.
     * Recibe un JSON con las credenciales y responde con los datos del usuario logueado.
     */
    @PostMapping("/login")
    public ResponseEntity<?> ingresar(@RequestBody LoginRequestDTO credentials) {
        
        // Extraemos los datos directamente de tu nuevo DTO
        String username = credentials.username();
        String password = credentials.password();

        // Validamos que los parámetros no vengan vacíos en la petición REST
        if (username == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Debe proporcionar obligatoriamente un usuario y una contraseña."));
        }

        // Ejecutamos tu lógica del servicio que interactúa con UsuarioDAO
        Usuario usuario = servicioUsuario.validarAcceso(username, password);

        if (usuario != null) {
            // Limpieza de seguridad: evitamos enviar el hash del password de vuelta al cliente
            usuario.setPassword(null);

            // Generar token JWT con expiración y rol
            String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());

            // Devolver token y datos del usuario (sin password)
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "usuario", usuario
            ));
        } else {
            // Si las credenciales fallan, respondemos con un error HTTP 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas. Verifique su usuario y contraseña."));
        }
    }

    /**
     * Endpoint: POST /api/v1/auth/logout
     * Reemplaza la sección "logout" de tu antiguo doGet.
     * En arquitecturas REST sin estado (Stateless), el backend no destruye sesiones en servidor.
     * Este endpoint le indica formalmente al frontend que debe destruir el token guardado en el navegador.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> salir() {
        // En una API REST, el cierre de sesión es responsabilidad del cliente borrando sus credenciales locales
        return ResponseEntity.ok(Map.of("mensaje", "Sesión finalizada. Recuerde eliminar el token en el cliente."));
    }

    /**
     * Endpoint: POST /api/v1/auth/recuperar-password
     * Genera una clave temporal y la devuelve al frontend.
     */
    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@RequestBody Map<String, String> payload) {
        try {
            String correo = payload.get("correo");
            if (correo == null || correo.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El correo electrónico es obligatorio."));
            }

            // Llamamos al servicio que hace toda la lógica y nos devuelve la clave generada
            String nuevaPassword = servicioUsuario.recuperarPassword(correo);

            // Devolvemos el mensaje de éxito inyectando la nueva clave para que el cliente la vea
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Tu contraseña temporal es: " + nuevaPassword + " (Cópiala e inicia sesión)"
            ));

        } catch (Exception e) {
            // Si el correo no existe, devolvemos un 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}