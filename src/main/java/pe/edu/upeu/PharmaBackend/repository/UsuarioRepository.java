
package pe.edu.upeu.PharmaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.PharmaBackend.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
