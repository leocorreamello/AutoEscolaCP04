package br.com.fiapCP04.Checkpoint04.service.validacao.agendamento;

import br.com.fiapCP04.Checkpoint04.domain.Aluno;
import br.com.fiapCP04.Checkpoint04.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP04.Checkpoint04.exception.RegraNegocioException;
import br.com.fiapCP04.Checkpoint04.repository.AlunoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAlunoAtivo implements ValidadorAgendamento {

    private final AlunoRepository repository;

    public ValidadorAlunoAtivo(AlunoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(InstrucaoAgendamentoRequest dados) {
        boolean ativo = repository.findById(dados.idAluno())
                .map(Aluno::getAtivo)
                .orElse(false);

        if (!ativo) {
            throw new RegraNegocioException("Nao e permitido agendar instrucao para aluno inativo");
        }
    }
}
