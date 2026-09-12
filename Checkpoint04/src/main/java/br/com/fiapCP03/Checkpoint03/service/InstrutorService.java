package br.com.fiapCP03.Checkpoint03.service;

import br.com.fiapCP03.Checkpoint03.domain.Endereco;
import br.com.fiapCP03.Checkpoint03.domain.Instrutor;
import br.com.fiapCP03.Checkpoint03.dto.EnderecoRequest;
import br.com.fiapCP03.Checkpoint03.dto.InstrutorCreateRequest;
import br.com.fiapCP03.Checkpoint03.dto.InstrutorUpdateRequest;
import br.com.fiapCP03.Checkpoint03.repository.InstrutorRepository;
import br.com.fiapCP03.Checkpoint03.vo.InstrutorListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InstrutorService {

    private final InstrutorRepository repository;

    public InstrutorService(InstrutorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public InstrutorListResponse criar(InstrutorCreateRequest request) {
        Instrutor instrutor = new Instrutor(
                request.nome(),
                request.email(),
                request.telefone(),
                request.cnh(),
                request.especialidade(),
                toEndereco(request.endereco())
        );

        Instrutor salvo = repository.save(instrutor);
        return toListResponse(salvo);
    }

    @Transactional(readOnly = true)
    public Page<InstrutorListResponse> listar(Pageable pageable) {
        return repository.findByAtivoTrue(pageable).map(this::toListResponse);
    }

    @Transactional
    public InstrutorListResponse atualizar(Long id, InstrutorUpdateRequest request) {
        Instrutor instrutor = repository.findById(id)
                .filter(Instrutor::getAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrutor nao encontrado"));

        instrutor.setNome(request.nome());
        instrutor.setTelefone(request.telefone());
        instrutor.setEndereco(toEndereco(request.endereco()));

        return toListResponse(repository.save(instrutor));
    }

    @Transactional
    public void excluir(Long id) {
        Instrutor instrutor = repository.findById(id)
                .filter(Instrutor::getAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instrutor nao encontrado"));

        instrutor.setAtivo(false);
        repository.save(instrutor);
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

    private InstrutorListResponse toListResponse(Instrutor instrutor) {
        return new InstrutorListResponse(
                instrutor.getId(),
                instrutor.getNome(),
                instrutor.getEmail(),
                instrutor.getCnh(),
                instrutor.getEspecialidade()
        );
    }
}

