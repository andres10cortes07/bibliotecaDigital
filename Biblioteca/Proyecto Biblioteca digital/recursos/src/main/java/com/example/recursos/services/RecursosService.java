package com.example.recursos.services;

import com.example.recursos.entities.Recurso;
import com.example.recursos.repository.RecursosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class RecursosService implements IRecursosService{

    @Autowired
    private RecursosRepository recursosRepository;

    public List<Recurso> getAll(){return (List<Recurso>) recursosRepository.findAll();}

    public Recurso getById(Long id) {
        return recursosRepository.findById(id)
                // se usa orElseThrow porque findById retorna un Optional
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
    }

    public Recurso create(Recurso newResource) {
        try {
            return recursosRepository.save(newResource);
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public Recurso update(Long id, Map<String, Object> dataUpdated) {
        Recurso recurso = recursosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        dataUpdated.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Recurso.class, key);
            if (field != null) {
                field.setAccessible(true);

                try {
                    // Conversión para el enum
                    if (field.getType().isEnum() && value instanceof String) {
                        Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), ((String) value).toLowerCase());
                        ReflectionUtils.setField(field, recurso, enumValue);
                    }
                    // Conversión para LocalDate
                    else if (field.getType().equals(LocalDate.class) && value instanceof String) {
                        LocalDate date = LocalDate.parse((String) value);
                        ReflectionUtils.setField(field, recurso, date);
                    }
                    else {
                        ReflectionUtils.setField(field, recurso, value);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error al actualizar el campo: " + key);
                }
            }
        });

        try {
            return recursosRepository.save(recurso);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public void delete(Long id) {
        Recurso user = recursosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        recursosRepository.delete(user);
    }

}
