package br.com.fiapCP04.Checkpoint04.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InstrucaoAgendamentoRequest(
        @NotNull Long idAluno,
        Long idInstrutor,
        @NotNull LocalDateTime dataHora
) {
}
