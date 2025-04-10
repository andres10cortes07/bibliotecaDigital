package com.example.usuarios.services;

import com.example.usuarios.entities.Usuario;
import com.example.usuarios.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class UsuariosService implements IUsuariosService{

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<Usuario> getAll(){return (List<Usuario>) usuariosRepository.findAll();}

    public Usuario getById(Long id) {
        return usuariosRepository.findById(id)
                // se usa orElseThrow porque findById retorna un Optional
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario create(Usuario newUser) {
        try {
            // Encriptar la contraseña antes de guardar
            String contrasenaEncriptada = passwordEncoder.encode(newUser.getContrasena());
            newUser.setContrasena(contrasenaEncriptada);
            return usuariosRepository.save(newUser);
        }
        catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Campos inválidos");
        }
    }

    public Usuario update(Long id, Map<String, Object> dataUpdated) {
        Usuario user = usuariosRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        dataUpdated.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Usuario.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
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
