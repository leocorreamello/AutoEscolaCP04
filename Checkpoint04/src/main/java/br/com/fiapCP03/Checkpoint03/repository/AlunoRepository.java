package br.com.fiapCP03.Checkpoint03.repository;

import br.com.fiapCP03.Checkpoint03.domain.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Page<Aluno> findByAtivoTrue(Pageable pageable);
}

