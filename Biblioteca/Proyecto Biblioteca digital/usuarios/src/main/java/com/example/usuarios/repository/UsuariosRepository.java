package com.example.usuarios.repository;

import com.example.usuarios.entities.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuariosRepository extends CrudRepository<Usuario, Long>{
    Optional<Usuario> findByEmail(String email);
}