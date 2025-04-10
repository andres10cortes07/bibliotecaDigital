package com.example.reservas.services;

import com.example.ejemplares.entities.Ejemplar;
import com.example.reservas.entities.Reserva;
import com.example.reservas.repository.ReservasRepository;
import com.example.usuarios.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import com.example.usuarios.repository.UsuariosRepository;
import com.example.ejemplares.repository.EjemplaresRepository;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReservasService implements IReservasService{

    @Autowired
    private ReservasRepository reservasRepository;

    @Autowired
    private EjemplaresRepository ejemplaresRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    public List<Reserva> getAll() {return (List<Reserva>) reservasRepository.findAll();}

    public Reserva getById (Long id) {
        return reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public List<Reserva> getByUserId (Long userId) {
        try {
            return reservasRepository.findByUsuario_Id(userId);
        }
        catch (DataIntegrityViolationException e){
            throw new RuntimeException("error");
        }
    }

    public List<Reserva> getByCopyId (Long copyId) {
        try {
            return reservasRepository.findByEjemplar_Id(copyId);
        }
        catch (DataIntegrityViolationException e){
            throw new RuntimeException("error");
        }
    }

    public Reserva create(Reserva newLoan) {
        try {
            return reservasRepository.save(newLoan);
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public Reserva update(Long id, Map<String, Object> dataUpdated) {
        Reserva reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        for (Map.Entry<String, Object> entry : dataUpdated.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Field field = ReflectionUtils.findField(Reserva.class, key);
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
                            ReflectionUtils.setField(field, reserva, usuario);
                        }
                    } else if ("ejemplar".equals(key) && value instanceof Map) {
                        Map<?, ?> ejemplarData = (Map<?, ?>) value;
                        Object ejemplarId = ejemplarData.get("id");
                        if (ejemplarId instanceof Number) {
                            Ejemplar ejemplar = ejemplaresRepository.findById(((Number) ejemplarId).longValue())
                                    .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
                            ReflectionUtils.setField(field, reserva, ejemplar);
                        }
                    }
                    // Enums
                    else if (field.getType().isEnum() && value instanceof String) {
                        Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), ((String) value));
                        ReflectionUtils.setField(field, reserva, enumValue);
                    }
                    // Fechas
                    else if (field.getType().equals(LocalDate.class) && value instanceof String) {
                        LocalDate date = LocalDate.parse((String) value);
                        ReflectionUtils.setField(field, reserva, date);
                    }
                    // Otros campos
                    else {
                        ReflectionUtils.setField(field, reserva, value);
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error al actualizar el campo: " + key + " -> " + e.getMessage());
                }
            }
        }

        return reservasRepository.save(reserva);
    }

    public void delete (Long id) {
        Reserva reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reservasRepository.delete(reserva);
    }

}
