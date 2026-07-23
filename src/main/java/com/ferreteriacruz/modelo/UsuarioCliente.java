package com.ferreteriacruz.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios_clientes")
public class UsuarioCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_cliente")
    private int idUsuarioCliente;

    @Column(name = "id_usuario", nullable = false)
    private int idUsuario;

    @Column(name = "dni", length = 20, nullable = false)
    private String dni;

    @Column(name = "nombre_completo", length = 150, nullable = false)
    private String nombreCompleto;

    @Column(name = "apellido", length = 150, nullable = false)
    private String apellido;

    @Column(name = "correo", length = 100, nullable = false)
    private String correo;

    public UsuarioCliente() {}

    // Getters y Setters
    public int getIdUsuarioCliente() { return idUsuarioCliente; }
    public void setIdUsuarioCliente(int idUsuarioCliente) { this.idUsuarioCliente = idUsuarioCliente; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}