package br.com.fiapCP03.Checkpoint03.vo;

import br.com.fiapCP03.Checkpoint03.domain.Especialidade;

public record InstrutorListResponse(
        Long id,
        String nome,
        String email,
        String cnh,
        Especialidade especialidade
) {
}

