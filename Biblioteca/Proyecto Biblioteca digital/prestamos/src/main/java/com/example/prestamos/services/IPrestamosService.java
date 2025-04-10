package com.example.prestamos.services;

import com.example.prestamos.entities.Prestamo;

import java.util.List;
import java.util.Map;

public interface IPrestamosService {

    List<Prestamo> getAll();

    Prestamo getById (Long id);

    List<Prestamo> getByUserId (Long userId);

    List<Prestamo> getByCopyId (Long copyId);

    Prestamo create (Prestamo newLoan);

    Prestamo update (Long id, Map<String, Object> dataUpdated);

    void delete (Long id);
}
