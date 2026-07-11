package com.ferreteriacruz.dao;

import com.ferreteriacruz.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Integer> {
    // Al heredar de JpaDAO, automáticamente hay:
    // save(categoria) -> Para insertar o actualizar
    // findAll() -> Para listar todas
    // deleteById(id) -> Para eliminar
    // findById(id) -> Para buscar una por ID
}