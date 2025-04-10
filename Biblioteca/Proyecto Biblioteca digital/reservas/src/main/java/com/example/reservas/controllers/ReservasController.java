package com.example.reservas.controllers;

import com.example.reservas.entities.Reserva;
import com.example.reservas.services.IReservasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservas")
public class ReservasController {

    @Autowired
    private IReservasService reservasService;

    @GetMapping("/getAll")
    public List<Reserva> getAll() {return reservasService.getAll();}

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById (@PathVariable Long id) {
        try {
            Reserva reserva = reservasService.getById(id);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<?> getByUserId (@PathVariable Long userId) {
        try {
            List<Reserva> reservas = reservasService.getByUserId(userId);
            return ResponseEntity.ok(reservas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getByCopyId/{copyId}")
    public ResponseEntity<?> getByCopyId (@PathVariable Long copyId) {
        try {
            List<Reserva> reservas = reservasService.getByCopyId(copyId);
            return ResponseEntity.ok(reservas);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create (@RequestBody Reserva newReserve) {
        try {
            Reserva nuevaReserva = reservasService.create(newReserve);
            return ResponseEntity.ok(nuevaReserva);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update (@PathVariable Long id, @RequestBody Map<String, Object> dataUpdated) {
        try {
            Reserva reserveUpdated = reservasService.update(id, dataUpdated);
            return ResponseEntity.ok(reserveUpdated);
        }
        catch (RuntimeException e) {
            String messageError = e.getMessage();
            //validación si es error de que no encontró el ID o si los nuevos campos están mal
            if (messageError.equals("Reserva no encontrada")) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageError);}
            else if (messageError.equals("Campos inválidos")) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageError);}
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            reservasService.delete(id);
            return ResponseEntity.ok("Reserva eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
































