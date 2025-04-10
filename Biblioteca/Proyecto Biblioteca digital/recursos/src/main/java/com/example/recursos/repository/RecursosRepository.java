package com.example.recursos.repository;

import com.example.recursos.entities.Recurso;
import org.springframework.data.repository.CrudRepository;

public interface RecursosRepository extends CrudRepository<Recurso, Long> {
}