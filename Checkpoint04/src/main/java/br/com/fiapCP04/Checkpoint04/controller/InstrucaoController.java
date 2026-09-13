package br.com.fiapCP04.Checkpoint04.controller;

import br.com.fiapCP04.Checkpoint04.dto.InstrucaoAgendamentoRequest;
import br.com.fiapCP04.Checkpoint04.dto.InstrucaoCancelamentoRequest;
import br.com.fiapCP04.Checkpoint04.service.InstrucaoService;
import br.com.fiapCP04.Checkpoint04.vo.InstrucaoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instrucoes")
public class InstrucaoController {

    private final InstrucaoService service;

    public InstrucaoController(InstrucaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstrucaoResponse agendar(@Valid @RequestBody InstrucaoAgendamentoRequest request) {
        return service.agendar(request);
    }

    @GetMapping
    public Page<InstrucaoResponse> listar(
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.listar(pageable);
    }

    @PatchMapping("/{id}/cancelamento")
    public InstrucaoResponse cancelar(
            @PathVariable Long id,
            @Valid @RequestBody InstrucaoCancelamentoRequest request
    ) {
        return service.cancelar(id, request);
    }
}
