package br.com.fiapCP03.Checkpoint03.service.validacao.agendamento;

import br.com.fiapCP03.Checkpoint03.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class ValidadorAntecedenciaAgendamento implements ValidadorAgendamento {

    private static final long ANTECEDENCIA_MINIMA_MINUTOS = 30;

    private final Clock clock;

    public ValidadorAntecedenciaAgendamento(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        LocalDateTime limite = LocalDateTime.now(clock).plusMinutes(ANTECEDENCIA_MINIMA_MINUTOS);

        if (dados.dataHora().isBefore(limite)) {
            throw new RegraNegocioException("A instrucao deve ser agendada com antecedencia minima de 30 minutos");
        }
    }
}
