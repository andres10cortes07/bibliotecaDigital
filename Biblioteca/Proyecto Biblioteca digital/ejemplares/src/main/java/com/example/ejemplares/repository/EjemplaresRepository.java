package com.example.ejemplares.repository;

import com.example.ejemplares.entities.Ejemplar;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EjemplaresRepository extends CrudRepository<Ejemplar, Long> {
    List<Ejemplar> findByRecursoId(Long recursoId);
}
