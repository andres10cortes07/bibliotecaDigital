package com.example.prestamos.services;

import com.example.ejemplares.entities.Ejemplar;
import com.example.ejemplares.repository.EjemplaresRepository;
import com.example.prestamos.entities.Prestamo;
import com.example.prestamos.repository.PrestamosRepository;
import com.example.usuarios.entities.Usuario;
import com.example.usuarios.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PrestamosService implements IPrestamosService {

    @Autowired
    private PrestamosRepository prestamosRepository;

    @Autowired
    private EjemplaresRepository ejemplaresRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    public List<Prestamo> getAll() {return (List<Prestamo>) prestamosRepository.findAll();}

    public Prestamo getById (Long id) {
        return prestamosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));
    }

    public List<Prestamo> getByUserId (Long userId) {
        try {
            return prestamosRepository.findByUsuario_Id(userId);
        }
        catch (DataIntegrityViolationException e){
            throw new RuntimeException("error");
        }
    }

    public List<Prestamo> getByCopyId (Long copyId) {
        try {
            return prestamosRepository.findByEjemplar_Id(copyId);
        }
        catch (DataIntegrityViolationException e){
            throw new RuntimeException("error");
        }
    }

    public Prestamo create(Prestamo newLoan) {
        try {
            return prestamosRepository.save(newLoan);
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public Prestamo update(Long id, Map<String, Object> dataUpdated) {
        Prestamo prestamo = prestamosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        for (Map.Entry<String, Object> entry : dataUpdated.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Field field = ReflectionUtils.findField(Prestamo.class, key);
            if (field != null) {
                field.setAccessible(true);
                try {
                    // Relaciones
                    if ("usuario".equals(key) && value instanceof Map) {
                        Map<?, ?> usuarioData = (Map<?, ?>) value;
                        Object usuarioId = usuarioData.get("id");
                        if (usuarioId instanceof Number) {
                            Usuario usuario = usuariosRepository.findById(((Number) usuarioId).longValue())
                                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                            ReflectionUtils.setField(field, prestamo, usuario);
                        }
                    } else if ("ejemplar".equals(key) && value instanceof Map) {
                        Map<?, ?> ejemplarData = (Map<?, ?>) value;
                        Object ejemplarId = ejemplarData.get("id");
                        if (ejemplarId instanceof Number) {
                            Ejemplar ejemplar = ejemplaresRepository.findById(((Number) ejemplarId).longValue())
                                    .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
                            ReflectionUtils.setField(field, prestamo, ejemplar);
                        }
                    }
                    // Enums
                    else if (field.getType().isEnum() && value instanceof String) {
                        Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), ((String) value));
                        ReflectionUtils.setField(field, prestamo, enumValue);
                    }
                    // Fechas
                    else if (field.getType().equals(LocalDate.class) && value instanceof String) {
                        LocalDate date = LocalDate.parse((String) value);
                        ReflectionUtils.setField(field, prestamo, date);
                    }
                    // Otros campos
                    else {
                        ReflectionUtils.setField(field, prestamo, value);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error al actualizar el campo: " + key + " -> " + e.getMessage());
                }
            }
        }

        return prestamosRepository.save(prestamo);
    }

    public void delete (Long id) {
        Prestamo prestamo = prestamosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));

        prestamosRepository.delete(prestamo);
    }

}
