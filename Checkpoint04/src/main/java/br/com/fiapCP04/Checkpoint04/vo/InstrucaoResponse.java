package br.com.fiapCP04.Checkpoint04.vo;

import br.com.fiapCP04.Checkpoint04.domain.MotivoCancelamento;

import java.time.LocalDateTime;

public record InstrucaoResponse(
        Long id,
        Long idAluno,
        String nomeAluno,
        Long idInstrutor,
        String nomeInstrutor,
        LocalDateTime dataHora,
        boolean cancelada,
        MotivoCancelamento motivoCancelamento
) {
}
