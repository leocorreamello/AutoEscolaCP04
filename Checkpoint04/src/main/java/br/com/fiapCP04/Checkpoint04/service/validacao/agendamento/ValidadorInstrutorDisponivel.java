package br.com.fiapCP04.Checkpoint04.service.validacao.agendamento;

import br.com.fiapCP04.Checkpoint04.domain.Instrucao;
import br.com.fiapCP04.Checkpoint04.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP04.Checkpoint04.exception.RegraNegocioException;
import br.com.fiapCP04.Checkpoint04.repository.InstrucaoRepository;
import org.springframework.stereotype.Component;

/**
 * Considera conflito qualquer instrucao nao cancelada do instrutor que se sobreponha
 * a janela de 1 hora da nova instrucao (ex.: 10:00 conflita com 10:30, mas nao com 11:00).
 */
@Component
public class ValidadorInstrutorDisponivel implements ValidadorAgendamento {

    private final InstrucaoRepository repository;

    public ValidadorInstrutorDisponivel(InstrucaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        if (dados.idInstrutor() == null) {
            return;
        }

        boolean ocupado = repository.instrutorPossuiInstrucaoEntre(
                dados.idInstrutor(),
                dados.dataHora().minusHours(Instrucao.DURACAO_HORAS),
                dados.dataHora().plusHours(Instrucao.DURACAO_HORAS)
        );

        if (ocupado) {
            throw new RegraNegocioException("O instrutor ja possui outra instrucao agendada nessa data/hora");
        }
    }
}
