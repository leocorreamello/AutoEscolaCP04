package br.com.fiapCP04.Checkpoint04.dto;

import br.com.fiapCP04.Checkpoint04.domain.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record InstrucaoCancelamentoRequest(
        @NotNull(message = "o motivo do cancelamento e obrigatorio") MotivoCancelamento motivo
) {
}
