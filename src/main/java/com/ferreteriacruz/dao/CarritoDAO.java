package com.ferreteriacruz.dao;

import com.ferreteriacruz.modelo.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoDAO extends JpaRepository<CarritoItem, Integer> {
    
    // Obtener todo el carrito de un cliente específico
    List<CarritoItem> findByIdUsuario(int idUsuario);
    
    // Buscar si un producto específico ya está en el carrito de ese cliente
    Optional<CarritoItem> findByIdUsuarioAndIdProducto(int idUsuario, int idProducto);

    // Vaciar el carrito tras realizar una compra exitosa
    @Transactional
    @Modifying
    void deleteByIdUsuario(int idUsuario);
}