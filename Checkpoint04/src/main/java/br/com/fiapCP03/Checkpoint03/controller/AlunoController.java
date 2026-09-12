package br.com.fiapCP03.Checkpoint03.controller;

import br.com.fiapCP03.Checkpoint03.dto.AlunoCreateRequest;
import br.com.fiapCP03.Checkpoint03.dto.AlunoUpdateRequest;
import br.com.fiapCP03.Checkpoint03.service.AlunoService;
import br.com.fiapCP03.Checkpoint03.vo.AlunoListResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlunoListResponse criar(@Valid @RequestBody AlunoCreateRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public Page<AlunoListResponse> listar(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.listar(pageable);
    }

    @PutMapping("/{id}")
    public AlunoListResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoUpdateRequest request
    ) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}

