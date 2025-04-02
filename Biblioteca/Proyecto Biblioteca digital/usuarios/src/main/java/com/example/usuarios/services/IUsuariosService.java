package com.example.usuarios.services;

import com.example.usuarios.entities.Usuario;

import java.util.List;
import java.util.Map;

public interface IUsuariosService {

    List<Usuario> getAll();

    Usuario getById(Long id);

    Usuario create(Usuario newUser);

    Usuario update(Long id, Map<String, Object> dataUpdated);

    void delete (Long id);
}
