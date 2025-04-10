package com.example.reservas.repository;

import com.example.reservas.entities.Reserva;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReservasRepository extends CrudRepository<Reserva, Long> {

    List<Reserva> findByUsuario_Id(Long usuarioId);

    List<Reserva> findByEjemplar_Id(Long ejemplarId);

}
