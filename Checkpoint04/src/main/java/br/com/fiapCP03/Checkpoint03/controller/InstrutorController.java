package br.com.fiapCP03.Checkpoint03.controller;

import br.com.fiapCP03.Checkpoint03.dto.InstrutorCreateRequest;
import br.com.fiapCP03.Checkpoint03.dto.InstrutorUpdateRequest;
import br.com.fiapCP03.Checkpoint03.service.InstrutorService;
import br.com.fiapCP03.Checkpoint03.vo.InstrutorListResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/instrutores")
public class InstrutorController {

    private final InstrutorService service;

    public InstrutorController(InstrutorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrutorListResponse criar(@Valid @RequestBody InstrutorCreateRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public Page<InstrutorListResponse> listar(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.listar(pageable);
    }

    @PutMapping("/{id}")
    public InstrutorListResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody InstrutorUpdateRequest request
    ) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}

