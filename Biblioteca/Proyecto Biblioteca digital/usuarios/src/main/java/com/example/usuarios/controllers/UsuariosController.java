package com.example.usuarios.controllers;

import com.example.usuarios.dto.LoginRequest;
import com.example.usuarios.entities.Usuario;
import com.example.usuarios.security.JwtUtil;
import com.example.usuarios.services.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String contrasena = loginRequest.get("contrasena");
        return usuariosService.login(email, contrasena);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Usuario newUser) {
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            newUser.setContrasena(encoder.encode(newUser.getContrasena()));

            Usuario usuario = usuariosService.create(newUser);

            String token = jwtUtil.generateToken(usuario.getEmail()); // Asegúrate de tener JwtUtil con generateToken

            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("usuario", usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public List<Usuario> getAll() {return usuariosService.getAll();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Usuario usuario = usuariosService.getById(id);
            return ResponseEntity.ok(usuario);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> dataUpdated) {

        try {
            Usuario userUpdated = usuariosService.update(id, dataUpdated);
            return ResponseEntity.ok(userUpdated);
        }
        catch (RuntimeException e) {
            String messageError = e.getMessage();
            //validación si es error de que no encontró el ID o si los nuevos campos están mal
            if (messageError.equals("Usuario no encontrado")) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageError);}
            else if (messageError.equals("Campos inválidos")) {return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageError);}
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocurrió un error inesperado");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            usuariosService.delete(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (RuntimeException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
