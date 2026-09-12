package br.com.fiapCP03.Checkpoint03.service.validacao.agendamento;

import br.com.fiapCP03.Checkpoint03.domain.Instrucao;
import br.com.fiapCP03.Checkpoint03.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
import br.com.fiapCP03.Checkpoint03.repository.InstrucaoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAlunoSemConflitoHorario implements ValidadorAgendamento {

    private final InstrucaoRepository repository;

    public ValidadorAlunoSemConflitoHorario(InstrucaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        boolean ocupado = repository.alunoPossuiInstrucaoEntre(
                dados.idAluno(),
                dados.dataHora().minusHours(Instrucao.DURACAO_HORAS),
                dados.dataHora().plusHours(Instrucao.DURACAO_HORAS)
        );

        if (ocupado) {
            throw new RegraNegocioException("O aluno ja possui outra instrucao agendada nessa data/hora");
        }
    }
}
