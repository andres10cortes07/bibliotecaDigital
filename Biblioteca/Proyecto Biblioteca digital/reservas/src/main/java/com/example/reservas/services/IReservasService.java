package com.example.reservas.services;

import com.example.reservas.entities.Reserva;

import java.util.List;
import java.util.Map;

public interface IReservasService {

    List<Reserva> getAll();

    Reserva getById (Long id);

    List<Reserva> getByUserId (Long userId);

    List<Reserva> getByCopyId (Long copyId);

    Reserva create (Reserva newReserve);

    Reserva update (Long id, Map<String, Object> dataUpdated);

    void delete (Long id);

}
