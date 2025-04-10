package com.example.ejemplares.services;

import com.example.ejemplares.entities.Ejemplar;

import java.util.List;
import java.util.Map;

public interface IEjemplaresService {

    List<Ejemplar> getAll();

    Ejemplar getById(Long id);

    List<Ejemplar> getByResourceId(Long id);

    Ejemplar create(Ejemplar newCopy);

    Ejemplar update(Long id, Map<String, Object> dataUpdated);

    void delete (Long id);

}
