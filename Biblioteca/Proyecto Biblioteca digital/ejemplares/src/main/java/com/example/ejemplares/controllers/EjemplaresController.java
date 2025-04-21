package com.example.ejemplares.controllers;

import com.example.ejemplares.entities.Ejemplar;
import com.example.ejemplares.services.IEjemplaresService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/ejemplares")
public class EjemplaresController {

    @Autowired
    private IEjemplaresService ejemplaresService;

    @GetMapping("/getAll")
    public List<Ejemplar> getAll() {return ejemplaresService.getAll();}

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Ejemplar ejemplar = ejemplaresService.getById(id);
            return ResponseEntity.ok(ejemplar);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getByResourceId/{id}")
    public ResponseEntity<?> getByResourceId (@PathVariable Long id){
        try {
            List<Ejemplar> ejemplar = ejemplaresService.getByResourceId(id);
            return ResponseEntity.ok(ejemplar);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Ejemplar newCopy, BindingResult result) {
        if (result.hasErrors()) {
            // Retorna el primer mensaje de error encontrado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(result.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            Ejemplar ejemplar = ejemplaresService.create(newCopy);
            return ResponseEntity.status(201).body(ejemplar);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> dataUpdated) {

        try {
            Ejemplar copyUpdated = ejemplaresService.update(id, dataUpdated);
            return ResponseEntity.ok(copyUpdated);
        }
        catch (RuntimeException e) {
            String messageError = e.getMessage();
            //validación si es error de que no encontró el ID o si los nuevos campos están mal
            if (messageError.equals("Ejemplar no encontrado")) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageError);}
            else if (messageError.equals("Campos inválidos")) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageError);}
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            ejemplaresService.delete(id);
            return ResponseEntity.ok("Ejemplar eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
