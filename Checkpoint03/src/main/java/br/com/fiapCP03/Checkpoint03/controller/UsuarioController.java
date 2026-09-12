package br.com.fiapCP03.Checkpoint03.controller;

import br.com.fiapCP03.Checkpoint03.domain.Usuario;
import br.com.fiapCP03.Checkpoint03.dto.AlterarSenhaRequest;
import br.com.fiapCP03.Checkpoint03.dto.UsuarioCreateRequest;
import br.com.fiapCP03.Checkpoint03.dto.UsuarioUpdateRequest;
import br.com.fiapCP03.Checkpoint03.service.UsuarioService;
import br.com.fiapCP03.Checkpoint03.vo.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioCreateRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public Page<UsuarioResponse> listar(
            @PageableDefault(size = 10, sort = "login", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.listar(pageable);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            @AuthenticationPrincipal Usuario autenticado
    ) {
        return service.atualizarPerfil(id, request, autenticado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, @AuthenticationPrincipal Usuario autenticado) {
        service.excluir(id, autenticado);
    }

    @PatchMapping("/me/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarPropriaSenha(
            @Valid @RequestBody AlterarSenhaRequest request,
            @AuthenticationPrincipal Usuario autenticado
    ) {
        service.alterarSenha(autenticado, request);
    }
}
