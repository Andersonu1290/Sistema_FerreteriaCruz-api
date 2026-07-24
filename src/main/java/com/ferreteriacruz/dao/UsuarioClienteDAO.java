package com.ferreteriacruz.dao;

import com.ferreteriacruz.modelo.UsuarioCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioClienteDAO extends JpaRepository<UsuarioCliente, Integer> {
    // Para cuando queramos buscar los datos personales usando el ID del login
    Optional<UsuarioCliente> findByIdUsuario(int idUsuario);

    // NUEVO: Para la recuperación de contraseña
    Optional<UsuarioCliente> findByCorreo(String correo);
}