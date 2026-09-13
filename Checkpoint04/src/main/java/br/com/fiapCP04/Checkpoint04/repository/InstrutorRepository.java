package br.com.fiapCP04.Checkpoint04.repository;

import br.com.fiapCP04.Checkpoint04.domain.Instrutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    Page<Instrutor> findByAtivoTrue(Pageable pageable);

    @Query("""
            select i from Instrutor i
            where i.ativo = true
              and not exists (
                  select ins.id from Instrucao ins
                  where ins.instrutor = i
                    and ins.motivoCancelamento is null
                    and ins.dataHora > :inicio
                    and ins.dataHora < :fim
              )
            """)
    List<Instrutor> findDisponiveisEntre(LocalDateTime inicio, LocalDateTime fim);
}

