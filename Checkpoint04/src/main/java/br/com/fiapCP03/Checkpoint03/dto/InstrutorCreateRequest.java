package br.com.fiapCP03.Checkpoint03.dto;

import br.com.fiapCP03.Checkpoint03.domain.Especialidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InstrutorCreateRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String telefone,
        @NotBlank String cnh,
        @NotNull Especialidade especialidade,
        @Valid @NotNull EnderecoRequest endereco
) {
}

