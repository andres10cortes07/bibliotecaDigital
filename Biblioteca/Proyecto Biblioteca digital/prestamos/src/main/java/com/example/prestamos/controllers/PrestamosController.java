package com.example.prestamos.controllers;

import com.example.ejemplares.entities.Ejemplar;
import com.example.prestamos.entities.Prestamo;
import com.example.prestamos.services.IPrestamosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prestamos")
public class PrestamosController {

    @Autowired
    private IPrestamosService prestamosService;

    @GetMapping("/getAll")
    public List<Prestamo> getAll() {return prestamosService.getAll();}

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById (@PathVariable Long id) {
        try {
            Prestamo prestamo = prestamosService.getById(id);
            return ResponseEntity.ok(prestamo);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<?> getByUserId (@PathVariable Long userId) {
        try {
            List<Prestamo> prestamos = prestamosService.getByUserId(userId);
            return ResponseEntity.ok(prestamos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ENDPOINT PARA OBTENER PRÉSTAMOS POR EL ID DEL EJEMPLAR
    @GetMapping("/getByCopyId/{copyId}")
    public ResponseEntity<?> getByCopyId (@PathVariable Long copyId) {
        try {
            List<Prestamo> prestamos= prestamosService.getByCopyId(copyId);
            return ResponseEntity.ok(prestamos);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> create (@RequestBody Prestamo newLoan) {
        try {
            Prestamo nuevoPrestamo = prestamosService.create(newLoan);
            return ResponseEntity.ok(nuevoPrestamo);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update (@PathVariable Long id, @RequestBody Map<String, Object> dataUpdated) {
        try {
            Prestamo loanUpdated = prestamosService.update(id, dataUpdated);
            return ResponseEntity.ok(loanUpdated);
        }
        catch (RuntimeException e) {
            String messageError = e.getMessage();
            //validación si es error de que no encontró el ID o si los nuevos campos están mal
            if (messageError.equals("Prestamo no encontrado")) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageError);}
            else if (messageError.equals("Campos inválidos")) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageError);}
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            prestamosService.delete(id);
            return ResponseEntity.ok("Prestamo eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
