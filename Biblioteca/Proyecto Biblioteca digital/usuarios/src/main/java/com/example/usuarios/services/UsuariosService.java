package com.example.usuarios.services;

import com.example.usuarios.entities.Usuario;
import com.example.usuarios.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuariosService implements IUsuariosService{

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> login (String email, String contrasena) {
        Optional<Usuario> usuarioOptional = usuariosRepository.findByEmail(email);

        if (!usuarioOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        boolean coincide = passwordEncoder.matches(contrasena, usuario.getContrasena());

        System.out.println("Contraseña ingresada: " + contrasena);
        System.out.println("Contraseña en BD: " + usuario.getContrasena());
        System.out.println("Coinciden?: " + coincide);

        if (!coincide) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        return ResponseEntity.ok(usuario); // o devolver token si usas JWT
    }

    public List<Usuario> getAll(){return (List<Usuario>) usuariosRepository.findAll();}

    public Usuario getById(Long id) {
        return usuariosRepository.findById(id)
                // se usa orElseThrow porque findById retorna un Optional
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public Usuario create(Usuario newUser) {
        // ⚠️ Solo encriptar si la contraseña aún NO está encriptada
        String contrasenaOriginal = newUser.getContrasena();

        // Opcional: evitar doble encriptación si por error ya viene cifrada (no es común, pero por seguridad)
        if (!contrasenaOriginal.startsWith("$2a$")) {
            contrasenaOriginal = passwordEncoder.encode(contrasenaOriginal);
            newUser.setContrasena(contrasenaOriginal);
        }

        return usuariosRepository.save(newUser);
    }


    public Usuario update(Long id, Map<String, Object> dataUpdated) {
        Usuario user = usuariosRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        dataUpdated.forEach((key, value) -> {
            if ("contrasena".equals(key)) {
                // Comprobamos si el valor es de tipo String
                if (value instanceof String) {
                    String contrasena = (String) value;
                    String contrasenaEncriptada = passwordEncoder.encode(contrasena);
                    user.setContrasena(contrasenaEncriptada);
                }
            } else {
                Field field = ReflectionUtils.findField(Usuario.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, user, value);
                }
            }
        });

            try {
                return usuariosRepository.save(user);
            }
            catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Campos inválidos");
            }
    }

    public void delete(Long id) {
        Usuario user = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuariosRepository.delete(user);
    }
}
