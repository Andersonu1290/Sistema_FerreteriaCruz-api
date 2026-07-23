package com.ferreteriacruz.servicio;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferreteriacruz.modelo.Usuario;
import com.ferreteriacruz.modelo.UsuarioCliente;
import com.ferreteriacruz.dao.UsuarioDAO;
import com.ferreteriacruz.dao.UsuarioClienteDAO;
import com.ferreteriacruz.dto.RegistroClienteDTO;
import com.google.common.base.Preconditions;

@Service // Marca esta clase como un componente de servicio de Spring
public class ServicioUsuario {

    // Logback (vía SLF4J) - Para auditoría de seguridad
    private static final Logger log = LoggerFactory.getLogger(ServicioUsuario.class);

    private final UsuarioDAO usuarioDAO;
    private final UsuarioClienteDAO usuarioClienteDAO;
    private final PasswordEncoder passwordEncoder;

    // Inyección de dependencias por constructor (Recomendado en Spring)
    @Autowired
    public ServicioUsuario(UsuarioDAO usuarioDAO, UsuarioClienteDAO usuarioClienteDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.usuarioClienteDAO = usuarioClienteDAO;
        this.passwordEncoder = passwordEncoder != null ? passwordEncoder : new BCryptPasswordEncoder();
    }

    public Usuario validarAcceso(String user, String pass) {
        try {
            // Guava: Validaciones defensivas contra payloads vacíos
            Preconditions.checkArgument(StringUtils.isNotBlank(user), "El usuario es obligatorio");
            Preconditions.checkArgument(StringUtils.isNotBlank(pass), "La contraseña es obligatoria");

            // Apache Commons: Limpiamos espacios accidentales al inicio o al final
            String cleanUser = StringUtils.trim(user);
            log.debug("Intentando validar acceso para el usuario: {}", cleanUser);

            // Buscamos por username y comparamos el hash con BCrypt
            Optional<Usuario> usuarioOpt = usuarioDAO.findByUsername(cleanUser);
            if (usuarioOpt.isPresent()) {
                Usuario u = usuarioOpt.get();
                if (passwordEncoder.matches(pass, u.getPassword())) {
                    log.info("Acceso concedido exitosamente para el rol {} - Usuario: {}", u.getRol(), cleanUser);
                    return u;
                } else {
                    log.warn("Alerta de seguridad: Intento de acceso fallido para usuario '{}' - Contraseña incorrecta", cleanUser);
                }
            } else {
                log.warn("Intento de acceso fallido: El usuario '{}' no existe en la base de datos", cleanUser);
            }
        } catch (IllegalArgumentException e) {
            log.error("Petición de login rechazada por datos inválidos: {}", e.getMessage());
        }
        
        return null;
    }

    public List<Usuario> obtenerListaPersonal() {
        return usuarioDAO.findAll(); // Método nativo de JpaDAO
    }

    public boolean registrarNuevoPersonal(Usuario usuario) {
        // Guava: Blindamos la creación de cuentas
        Preconditions.checkNotNull(usuario, "El objeto usuario no puede ser nulo");
        Preconditions.checkArgument(StringUtils.isNotBlank(usuario.getUsername()), "El nombre de usuario es obligatorio");
        Preconditions.checkArgument(StringUtils.isNotBlank(usuario.getPassword()), "La contraseña es obligatoria");

        // Apache Commons: Normalizamos el nombre de usuario
        String cleanUser = StringUtils.trim(usuario.getUsername());
        usuario.setUsername(cleanUser);

        if (usuarioDAO.existsByUsername(cleanUser)) {
            log.warn("Registro rechazado: El nombre de usuario '{}' ya se encuentra en uso", cleanUser);
            return false; // El usuario ya existe
        }
        
        log.info("Registrando nueva cuenta en el sistema para el usuario: {}", cleanUser);
        
        // Encriptamos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioDAO.save(usuario); // Método nativo
        return true;
    }

    // =========================================================
    // NUEVO MÉTODO FULL-STACK: Registro Real de Clientes
    // Mantiene el estándar estricto de Guava y Apache Commons
    // =========================================================
    @Transactional(rollbackFor = Exception.class)
    public Usuario registrarClienteCompleto(RegistroClienteDTO dto) throws Exception {
        Preconditions.checkNotNull(dto, "Los datos de registro no pueden ser nulos");
        Preconditions.checkArgument(StringUtils.isNotBlank(dto.username()), "El usuario es obligatorio");
        Preconditions.checkArgument(StringUtils.isNotBlank(dto.password()), "La contraseña es obligatoria");
        Preconditions.checkArgument(StringUtils.isNotBlank(dto.dni()), "El DNI es obligatorio");
        Preconditions.checkArgument(StringUtils.isNotBlank(dto.apellido()), "El apellido es obligatorio"); // NUEVO

        String cleanUser = StringUtils.trim(dto.username());
        
        if (usuarioDAO.existsByUsername(cleanUser)) {
            throw new Exception("El nombre de usuario ya existe.");
        }

        Usuario u = new Usuario();
        u.setUsername(cleanUser);
        u.setPassword(passwordEncoder.encode(dto.password()));
        u.setRol("CLIENTE");
        Usuario usuarioGuardado = usuarioDAO.save(u);

        UsuarioCliente uc = new UsuarioCliente();
        uc.setIdUsuario(usuarioGuardado.getIdUsuario());
        uc.setDni(StringUtils.trim(dto.dni()));
        uc.setNombreCompleto(StringUtils.normalizeSpace(dto.nombre()));
        uc.setApellido(StringUtils.normalizeSpace(dto.apellido()));
        uc.setCorreo(StringUtils.trim(dto.correo()).toLowerCase());
        usuarioClienteDAO.save(uc);

        return usuarioGuardado;
    }

    public UsuarioCliente obtenerPerfilCliente(int idUsuario) {
        return usuarioClienteDAO.findByIdUsuario(idUsuario).orElse(null);
    }
}