package br.com.fiapCP04.Checkpoint04.dto;

import br.com.fiapCP04.Checkpoint04.domain.Especialidade;
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

