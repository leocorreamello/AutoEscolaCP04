package br.com.fiapCP04.Checkpoint04.service.validacao.agendamento;

import br.com.fiapCP04.Checkpoint04.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP04.Checkpoint04.exception.RegraNegocioException;
import br.com.fiapCP04.Checkpoint04.repository.InstrucaoRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {

    private static final long LIMITE_INSTRUCOES_POR_DIA = 2;

    private final InstrucaoRepository repository;

    public ValidadorLimiteDiarioAluno(InstrucaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        LocalDate dia = dados.dataHora().toLocalDate();
        long quantidade = repository.contarInstrucoesDoAlunoEntre(
                dados.idAluno(),
                dia.atStartOfDay(),
                dia.plusDays(1).atStartOfDay()
        );

        if (quantidade >= LIMITE_INSTRUCOES_POR_DIA) {
            throw new RegraNegocioException("O aluno ja possui 2 instrucoes agendadas nesse dia");
        }
    }
}
