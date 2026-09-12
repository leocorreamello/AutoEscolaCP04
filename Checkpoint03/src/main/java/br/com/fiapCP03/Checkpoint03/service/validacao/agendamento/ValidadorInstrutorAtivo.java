package br.com.fiapCP03.Checkpoint03.service.validacao.agendamento;

import br.com.fiapCP03.Checkpoint03.domain.Instrutor;
import br.com.fiapCP03.Checkpoint03.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
import br.com.fiapCP03.Checkpoint03.repository.InstrutorRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorInstrutorAtivo implements ValidadorAgendamento {

    private final InstrutorRepository repository;

    public ValidadorInstrutorAtivo(InstrutorRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        if (dados.idInstrutor() == null) {
            return;
        }

        boolean ativo = repository.findById(dados.idInstrutor())
                .map(Instrutor::getAtivo)
                .orElse(false);

        if (!ativo) {
            throw new RegraNegocioException("Nao e permitido agendar instrucao com instrutor inativo");
        }
    }
}
