package br.com.fiapCP04.Checkpoint04.service.validacao.cancelamento;

import br.com.fiapCP04.Checkpoint04.domain.Instrucao;
import br.com.fiapCP04.Checkpoint04.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class ValidadorAntecedenciaCancelamento implements ValidadorCancelamento {

    private static final long ANTECEDENCIA_MINIMA_HORAS = 24;

    private final Clock clock;

    public ValidadorAntecedenciaCancelamento(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void validar(Instrucao instrucao) {
        LocalDateTime limite = LocalDateTime.now(clock).plusHours(ANTECEDENCIA_MINIMA_HORAS);

        if (instrucao.getDataHora().isBefore(limite)) {
            throw new RegraNegocioException("A instrucao somente pode ser cancelada com antecedencia minima de 24 horas");
        }
    }
}
