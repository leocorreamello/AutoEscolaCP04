package br.com.fiapCP03.Checkpoint03.service;

import br.com.fiapCP03.Checkpoint03.domain.Aluno;
import br.com.fiapCP03.Checkpoint03.domain.Instrucao;
import br.com.fiapCP03.Checkpoint03.domain.Instrutor;
import br.com.fiapCP03.Checkpoint03.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP03.Checkpoint03.dto.InstrucaoCancelamentoRequest;
import br.com.fiapCP03.Checkpoint03.exception.RegraNegocioException;
import br.com.fiapCP03.Checkpoint03.repository.AlunoRepository;
import br.com.fiapCP03.Checkpoint03.repository.InstrucaoRepository;
import br.com.fiapCP03.Checkpoint03.repository.InstrutorRepository;
import br.com.fiapCP03.Checkpoint03.service.validacao.agendamento.ValidadorAgendamento;
import br.com.fiapCP03.Checkpoint03.service.validacao.cancelamento.ValidadorCancelamento;
import br.com.fiapCP03.Checkpoint03.vo.InstrucaoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class InstrucaoService {

    private final InstrucaoRepository repository;
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final List<ValidadorAgendamento> validadoresAgendamento;
    private final List<ValidadorCancelamento> validadoresCancelamento;

    public InstrucaoService(
            InstrucaoRepository repository,
            AlunoRepository alunoRepository,
            InstrutorRepository instrutorRepository,
            List<ValidadorAgendamento> validadoresAgendamento,
            List<ValidadorCancelamento> validadoresCancelamento
    ) {
        this.repository = repository;
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
        this.validadoresAgendamento = validadoresAgendamento;
        this.validadoresCancelamento = validadoresCancelamento;
    }

    @Transactional
    public InstrucaoResponse agendar(InstrucaoAgendamentoRequest request) {
        Aluno aluno = alunoRepository.findById(request.idAluno())
                .orElseThrow(() -> new RegraNegocioException("Aluno informado nao existe"));

        if (request.idInstrutor() != null && !instrutorRepository.existsById(request.idInstrutor())) {
            throw new RegraNegocioException("Instrutor informado nao existe");
        }

        validadoresAgendamento.forEach(validador -> validador.validar(request));

        Instrutor instrutor = escolherInstrutor(request);
        Instrucao instrucao = repository.save(new Instrucao(aluno, instrutor, request.dataHora()));
        return toResponse(instrucao);
    }

    @Transactional(readOnly = true)
    public Page<InstrucaoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public InstrucaoResponse cancelar(Long id, InstrucaoCancelamentoRequest request) {
        Instrucao instrucao = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrucao nao encontrada"));

        validadoresCancelamento.forEach(validador -> validador.validar(instrucao));

        instrucao.cancelar(request.motivo());
        return toResponse(instrucao);
    }

    private Instrutor escolherInstrutor(InstrucaoAgendamentoRequest request) {
        if (request.idInstrutor() != null) {
            return instrutorRepository.getReferenceById(request.idInstrutor());
        }

        LocalDateTime dataHora = request.dataHora();
        List<Instrutor> disponiveis = instrutorRepository.findDisponiveisEntre(
                dataHora.minusHours(Instrucao.DURACAO_HORAS),
                dataHora.plusHours(Instrucao.DURACAO_HORAS)
        );

        if (disponiveis.isEmpty()) {
            throw new RegraNegocioException("Nenhum instrutor disponivel na data/hora informada");
        }

        return disponiveis.get(ThreadLocalRandom.current().nextInt(disponiveis.size()));
    }

    private InstrucaoResponse toResponse(Instrucao instrucao) {
        return new InstrucaoResponse(
                instrucao.getId(),
                instrucao.getAluno().getId(),
                instrucao.getAluno().getNome(),
                instrucao.getInstrutor().getId(),
                instrucao.getInstrutor().getNome(),
                instrucao.getDataHora(),
                instrucao.isCancelada(),
                instrucao.getMotivoCancelamento()
        );
    }
}
