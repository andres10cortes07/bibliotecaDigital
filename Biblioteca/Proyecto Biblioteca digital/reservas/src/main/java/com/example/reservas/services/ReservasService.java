package com.example.reservas.services;

import com.example.ejemplares.entities.Ejemplar;
import com.example.ejemplares.repository.EjemplaresRepository;
import com.example.prestamos.entities.Prestamo;
import com.example.prestamos.repository.PrestamosRepository;
import com.example.reservas.entities.Reserva;
import com.example.reservas.repository.ReservasRepository;
import com.example.usuarios.entities.Usuario;
import com.example.usuarios.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReservasService implements IReservasService {

    @Autowired
    private ReservasRepository reservasRepository;

    @Autowired
    private EjemplaresRepository ejemplaresRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private PrestamosRepository prestamosRepository;

    public List<Reserva> getAll() {
        return (List<Reserva>) reservasRepository.findAll();
    }

    public Reserva getById(Long id) {
        return reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public List<Reserva> getByUserId(Long userId) {
        try {
            return reservasRepository.findByUsuario_Id(userId);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("error");
        }
    }

    public List<Reserva> getByCopyId(Long copyId) {
        try {
            return reservasRepository.findByEjemplar_Id(copyId);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("error");
        }
    }

    @Transactional
    public Reserva create(Reserva newReserve) {
        try {
            // Buscar el ejemplar real
            Ejemplar ejemplar = ejemplaresRepository.findById(newReserve.getEjemplar().getId())
                    .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

            // Buscar el usuario real
            Usuario usuario = usuariosRepository.findById(newReserve.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Reasignar las entidades completas
            newReserve.setEjemplar(ejemplar);
            newReserve.setUsuario(usuario);

            Reserva reservaGuardada = reservasRepository.save(newReserve);

            // Lógica del estado del ejemplar
            if (reservaGuardada.getEstado() == Reserva.Estado.activa) {
                ejemplar.setEstado(Ejemplar.Estado.reservado);
            } else if (reservaGuardada.getEstado() == Reserva.Estado.cancelada) {
                ejemplar.setEstado(Ejemplar.Estado.disponible);
            } else if (reservaGuardada.getEstado() == Reserva.Estado.completada) {
                ejemplar.setEstado(Ejemplar.Estado.prestado);

                // Crear el préstamo automáticamente
                Prestamo prestamo = new Prestamo();
                prestamo.setUsuario(usuario);
                prestamo.setEjemplar(ejemplar);
                prestamo.setFechaPrestamo(LocalDate.now());
                prestamo.setFechaDevolucion(LocalDate.now().plusMonths(1));
                prestamo.setEstado(Prestamo.Estado.activo);

                // Guardar el préstamo
                prestamosRepository.save(prestamo);
            }

            ejemplaresRepository.save(ejemplar);
            return reservaGuardada;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear la reserva: " + e.getMessage());
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

        Ejemplar ejemplar = reserva.getEjemplar();
        if (reserva.getEstado() == Reserva.Estado.activa) {
            ejemplar.setEstado(Ejemplar.Estado.reservado);
        } else if (reserva.getEstado() == Reserva.Estado.cancelada) {
            ejemplar.setEstado(Ejemplar.Estado.disponible);
        } else if (reserva.getEstado() == Reserva.Estado.completada) {
            ejemplar.setEstado(Ejemplar.Estado.prestado);
        }
        ejemplaresRepository.save(ejemplar);

        return reservasRepository.save(reserva);
    }

    public void delete(Long id) {
        Reserva reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        reservasRepository.delete(reserva);
    }
}