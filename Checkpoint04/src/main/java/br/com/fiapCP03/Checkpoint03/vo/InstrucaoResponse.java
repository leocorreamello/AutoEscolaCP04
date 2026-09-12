package br.com.fiapCP03.Checkpoint03.vo;

import br.com.fiapCP03.Checkpoint03.domain.MotivoCancelamento;

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
