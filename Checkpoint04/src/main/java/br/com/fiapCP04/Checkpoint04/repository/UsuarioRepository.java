package br.com.fiapCP04.Checkpoint04.repository;

import br.com.fiapCP04.Checkpoint04.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);
}
