package pe.edu.upeu.PharmaBackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;

@Configuration
public class InitialAdminConfig {
    @Bean
    ApplicationRunner createInitialAdmin(UsuarioRepository users, PasswordEncoder encoder,
        @Value("${app.initial-admin.username:}") String username,
        @Value("${app.initial-admin.password:}") String password) {
        return args -> {
            if (username.isBlank() || password.isBlank()) return;
            if (password.length() < 12) throw new IllegalStateException("ADMIN_PASSWORD debe tener al menos 12 caracteres");
            if (users.findByUsername(username).isPresent()) return;
            Usuario u=new Usuario();
            u.setUsername(username);u.setPasswordHash(encoder.encode(password));
            u.setNombreCompleto("Administrador PharmaSoft");u.setRol("ADMIN");u.setActivo(true);
            users.save(u);
        };
    }
}
