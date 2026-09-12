package br.com.fiapCP03.Checkpoint03.dto;

import br.com.fiapCP03.Checkpoint03.domain.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record InstrucaoCancelamentoRequest(
        @NotNull(message = "o motivo do cancelamento e obrigatorio") MotivoCancelamento motivo
) {
}
