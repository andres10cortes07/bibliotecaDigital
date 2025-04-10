package com.example.recursos.services;

import com.example.recursos.entities.Recurso;

import java.util.List;
import java.util.Map;

public interface IRecursosService {

    List<Recurso> getAll();

    Recurso getById(Long id);

    Recurso create(Recurso newResource);

    Recurso update(Long id, Map<String, Object> dataUpdated);

    void delete (Long id);
}
