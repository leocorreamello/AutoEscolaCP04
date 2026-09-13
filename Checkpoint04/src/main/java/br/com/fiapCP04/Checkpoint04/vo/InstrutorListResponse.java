package br.com.fiapCP04.Checkpoint04.vo;

import br.com.fiapCP04.Checkpoint04.domain.Especialidade;

public record InstrutorListResponse(
        Long id,
        String nome,
        String email,
        String cnh,
        Especialidade especialidade
) {
}

