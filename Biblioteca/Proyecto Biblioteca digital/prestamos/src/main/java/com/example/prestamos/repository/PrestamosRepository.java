package com.example.prestamos.repository;

import com.example.prestamos.entities.Prestamo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrestamosRepository extends CrudRepository<Prestamo, Long> {

    List<Prestamo> findByUsuario_Id(Long usuarioId);

    List<Prestamo> findByEjemplar_Id(Long ejemplarId);

}
