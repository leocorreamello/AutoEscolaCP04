package br.com.fiapCP03.Checkpoint03.repository;

import br.com.fiapCP03.Checkpoint03.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);
}
