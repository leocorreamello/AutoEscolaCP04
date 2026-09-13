package br.com.fiapCP04.Checkpoint04.repository;

import br.com.fiapCP04.Checkpoint04.domain.Instrucao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface InstrucaoRepository extends JpaRepository<Instrucao, Long> {

    @Override
    @EntityGraph(attributePaths = {"aluno", "instrutor"})
    Page<Instrucao> findAll(Pageable pageable);

    @Query("""
            select count(i) > 0 from Instrucao i
            where i.instrutor.id = :instrutorId
              and i.motivoCancelamento is null
              and i.dataHora > :inicio
              and i.dataHora < :fim
            """)
    boolean instrutorPossuiInstrucaoEntre(Long instrutorId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select count(i) > 0 from Instrucao i
            where i.aluno.id = :alunoId
              and i.motivoCancelamento is null
              and i.dataHora > :inicio
              and i.dataHora < :fim
            """)
    boolean alunoPossuiInstrucaoEntre(Long alunoId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select count(i) from Instrucao i
            where i.aluno.id = :alunoId
              and i.motivoCancelamento is null
              and i.dataHora >= :inicio
              and i.dataHora < :fim
            """)
    long contarInstrucoesDoAlunoEntre(Long alunoId, LocalDateTime inicio, LocalDateTime fim);
}
