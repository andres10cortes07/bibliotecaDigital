package com.example.recursos.controllers;

import com.example.recursos.entities.Recurso;
import com.example.recursos.services.IRecursosService;
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
@RequestMapping("/recursos")
public class RecursosController {

    @Autowired
    private IRecursosService recursosService;

    @GetMapping("/getAll")
    public List<Recurso> getAll() {return recursosService.getAll();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Recurso recurso = recursosService.getById(id);
            return ResponseEntity.ok(recurso);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Recurso newResource, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(result.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            Recurso recurso = recursosService.create(newResource);
            return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> dataUpdated) {

        try {
            Recurso resourceUpdated = recursosService.update(id, dataUpdated);
            return ResponseEntity.ok(resourceUpdated);
        }
        catch (RuntimeException e) {
            String messageError = e.getMessage();
            //validación si es error de que no encontró el ID o si los nuevos campos están mal
            if (messageError.equals("Recurso no encontrado")) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageError);}
            else if (messageError.equals("Campos inválidos")) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageError);}
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            recursosService.delete(id);
            return ResponseEntity.ok("Recurso eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
