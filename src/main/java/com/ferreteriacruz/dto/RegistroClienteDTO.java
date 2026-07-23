package com.ferreteriacruz.dto;

public record RegistroClienteDTO(
    String username,
    String password,
    String dni,
    String nombre,
    String apellido, // <-- AGREGADO
    String correo
) {}