package com.example.ejemplares.services;

import com.example.ejemplares.entities.Ejemplar;
import com.example.ejemplares.repository.EjemplaresRepository;
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
public class EjemplaresService implements IEjemplaresService{

    @Autowired
    private EjemplaresRepository ejemplaresRepository;

    @Autowired
    private RecursosRepository recursoRepository;

    public List<Ejemplar> getAll() {return (List<Ejemplar>) ejemplaresRepository.findAll();}

    public Ejemplar getById(Long id) {
        return ejemplaresRepository.findById(id)
                // se usa orElseThrow porque findById retorna un Optional
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
    }

    public List<Ejemplar> getByResourceId(Long id) {
        try {
            List<Ejemplar> ejemplares = ejemplaresRepository.findByRecursoId(id);
            if (ejemplares.isEmpty()) {
                throw new RuntimeException("No se encontraron ejemplares");
            }
            return ejemplares;
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("error");
        }
    }

    public Ejemplar create(Ejemplar newCopy) {
        try {
            return ejemplaresRepository.save(newCopy);
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public Ejemplar update(Long id, Map<String, Object> dataUpdated) {
        Ejemplar ejemplar = ejemplaresRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

        for (Map.Entry<String, Object> entry : dataUpdated.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Field field = ReflectionUtils.findField(Ejemplar.class, key);
            if (field != null) {
                field.setAccessible(true);

                try {
                    if ("recurso".equals(key) && value instanceof Map) {
                        Map<?, ?> recursoData = (Map<?, ?>) value;
                        Object recursoId = recursoData.get("id");
                        if (recursoId instanceof Number) {
                            Recurso recurso = recursoRepository.findById(((Number) recursoId).longValue())
                                    .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
                            ReflectionUtils.setField(field, ejemplar, recurso);
                        }
                    }
                    else if (field.getType().isEnum() && value instanceof String) {
                        Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), ((String) value));
                        ReflectionUtils.setField(field, ejemplar, enumValue);
                    }
                    else if (field.getType().equals(LocalDate.class) && value instanceof String) {
                        LocalDate date = LocalDate.parse((String) value);
                        ReflectionUtils.setField(field, ejemplar, date);
                    }
                    else {
                        ReflectionUtils.setField(field, ejemplar, value);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error al actualizar el campo: " + key + " -> " + e.getMessage());
                }
            }
        }

        try {
            return ejemplaresRepository.save(ejemplar);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public void delete(Long id) {
        Ejemplar ejemplar = ejemplaresRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

        ejemplaresRepository.delete(ejemplar);
    }

}
