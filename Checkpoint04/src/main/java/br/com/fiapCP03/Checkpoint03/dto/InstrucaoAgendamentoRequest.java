package br.com.fiapCP03.Checkpoint03.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InstrucaoAgendamentoRequest(
        @NotNull Long idAluno,
        Long idInstrutor,
        @NotNull LocalDateTime dataHora
) {
}
