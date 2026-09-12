package br.com.fiapCP03.Checkpoint03.service;

import br.com.fiapCP03.Checkpoint03.domain.Aluno;
import br.com.fiapCP03.Checkpoint03.domain.Endereco;
import br.com.fiapCP03.Checkpoint03.dto.AlunoCreateRequest;
import br.com.fiapCP03.Checkpoint03.dto.AlunoUpdateRequest;
import br.com.fiapCP03.Checkpoint03.dto.EnderecoRequest;
import br.com.fiapCP03.Checkpoint03.repository.AlunoRepository;
import br.com.fiapCP03.Checkpoint03.vo.AlunoListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AlunoService {

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AlunoListResponse criar(AlunoCreateRequest request) {
        Aluno aluno = new Aluno(
                request.nome(),
                request.email(),
                request.telefone(),
                request.cpf(),
                toEndereco(request.endereco())
        );

        Aluno salvo = repository.save(aluno);
        return toListResponse(salvo);
    }

    @Transactional(readOnly = true)
    public Page<AlunoListResponse> listar(Pageable pageable) {
        return repository.findByAtivoTrue(pageable).map(this::toListResponse);
    }

    @Transactional
    public AlunoListResponse atualizar(Long id, AlunoUpdateRequest request) {
        Aluno aluno = repository.findById(id)
                .filter(Aluno::getAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno nao encontrado"));

        aluno.setNome(request.nome());
        aluno.setTelefone(request.telefone());
        aluno.setEndereco(toEndereco(request.endereco()));

        return toListResponse(repository.save(aluno));
    }

    @Transactional
    public void excluir(Long id) {
        Aluno aluno = repository.findById(id)
                .filter(Aluno::getAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno nao encontrado"));

        aluno.setAtivo(false);
        repository.save(aluno);
    }

    private Endereco toEndereco(EnderecoRequest request) {
        return new Endereco(
                request.logradouro(),
                request.numero(),
                request.complemento(),
                request.bairro(),
                request.cidade(),
                request.uf(),
                request.cep()
        );
    }

    private AlunoListResponse toListResponse(Aluno aluno) {
        return new AlunoListResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getCpf()
        );
    }
}

