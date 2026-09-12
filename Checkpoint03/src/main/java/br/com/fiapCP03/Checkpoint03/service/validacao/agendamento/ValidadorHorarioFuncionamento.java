package br.com.fiapCP03.Checkpoint03.service.validacao.agendamento;

import br.com.fiapCP03.Checkpoint03.domain.Instrucao;
import br.com.fiapCP03.Checkpoint03.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Funcionamento de segunda a sabado, das 06:00 as 21:00. Como a instrucao dura 1 hora,
 * o ultimo horario de inicio possivel e 20:00.
 */
@Component
public class ValidadorHorarioFuncionamento implements ValidadorAgendamento {

    private static final LocalTime ABERTURA = LocalTime.of(6, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(21, 0);

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        boolean domingo = dados.dataHora().getDayOfWeek() == DayOfWeek.SUNDAY;
        LocalTime inicio = dados.dataHora().toLocalTime();
        LocalTime ultimoInicio = FECHAMENTO.minusHours(Instrucao.DURACAO_HORAS);

        if (domingo || inicio.isBefore(ABERTURA) || inicio.isAfter(ultimoInicio)) {
            throw new RegraNegocioException(
                    "Instrucao fora do horario de funcionamento (segunda a sabado, das 06:00 as 21:00)");
        }
    }
}
