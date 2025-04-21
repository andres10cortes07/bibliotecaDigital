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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PrestamosService implements IPrestamosService {

    @Autowired
    private PrestamosRepository prestamosRepository;

    @Autowired
    private EjemplaresRepository ejemplaresRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    public void verificarEstadoAtrasado(Prestamo prestamo) {
        if (prestamo.getFechaDevolucion() != null && prestamo.getFechaDevolucion().isBefore(LocalDate.now())) {
            prestamo.setEstado(Prestamo.Estado.atrasado);
        }
    }


    public List<Prestamo> getAll() {
        // Convertimos el Iterable a List utilizando Collectors
        List<Prestamo> prestamos = StreamSupport.stream(prestamosRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        for (Prestamo prestamo : prestamos) {
            verificarEstadoAtrasado(prestamo);
        }

        return prestamos;
    }


    public Prestamo getById(Long id) {
        Prestamo prestamo = prestamosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        verificarEstadoAtrasado(prestamo);

        return prestamo;
    }


    public List<Prestamo> getByUserId(Long userId) {
        try {
            // Convertimos el Iterable a List utilizando Collectors
            List<Prestamo> prestamos = StreamSupport.stream(prestamosRepository.findByUsuario_Id(userId).spliterator(), false)
                    .collect(Collectors.toList());

            // Verificamos si el préstamo está atrasado
            for (Prestamo prestamo : prestamos) {
                verificarEstadoAtrasado(prestamo);
            }

            return prestamos;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error al obtener los préstamos por usuario");
        }
    }


    public List<Prestamo> getByCopyId(Long copyId) {
        try {
            List<Prestamo> prestamos = StreamSupport.stream(prestamosRepository.findByEjemplar_Id(copyId).spliterator(), false)
                    .collect(Collectors.toList());

            // Verificamos si el préstamo está atrasado
            for (Prestamo prestamo : prestamos) {
                verificarEstadoAtrasado(prestamo);
            }

            return prestamos;
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error al obtener los préstamos por ejemplar");
        }
    }


    public Prestamo create(Prestamo newLoan) {
        try {
            // Guardamos el préstamo
            Prestamo savedLoan = prestamosRepository.save(newLoan);

            // Verificamos si el préstamo está atrasado
            verificarEstadoAtrasado(savedLoan);

            // Obtenemos el ejemplar relacionado
            Ejemplar ejemplar = ejemplaresRepository.findById(newLoan.getEjemplar().getId())
                    .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

            // Dependiendo del estado del préstamo, actualizamos el estado del ejemplar
            if (newLoan.getEstado() == Prestamo.Estado.activo) {
                ejemplar.setEstado(Ejemplar.Estado.prestado);
            } else if (newLoan.getEstado() == Prestamo.Estado.devuelto ||
                    newLoan.getEstado() == Prestamo.Estado.atrasado) {
                ejemplar.setEstado(Ejemplar.Estado.disponible);
            }

            ejemplaresRepository.save(ejemplar);

            return savedLoan;

        } catch (DataIntegrityViolationException e) {
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

        // Verificamos si el préstamo está atrasado después de actualizar los campos
        verificarEstadoAtrasado(prestamo);

        Prestamo updated = prestamosRepository.save(prestamo);

        // ACTUALIZAR EL ESTADO DEL EJEMPLAR RELACIONADO
        Ejemplar ejemplar = updated.getEjemplar();
        if (ejemplar != null) {
            if (updated.getEstado() == Prestamo.Estado.activo) {
                ejemplar.setEstado(Ejemplar.Estado.prestado);
            } else if (updated.getEstado() == Prestamo.Estado.devuelto ||
                    updated.getEstado() == Prestamo.Estado.atrasado) {
                ejemplar.setEstado(Ejemplar.Estado.disponible);
            }
            ejemplaresRepository.save(ejemplar);
        }

        return updated;
    }


    public void delete (Long id) {
        Prestamo prestamo = prestamosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));

        prestamosRepository.delete(prestamo);
    }

}
