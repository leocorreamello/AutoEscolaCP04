package br.com.fiapCP04.Checkpoint04.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record InstrutorUpdateRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Valid @NotNull EnderecoRequest endereco,
        @Null(message = "nao e permitido alterar o e-mail do instrutor") String email,
        @Null(message = "nao e permitido alterar a CNH do instrutor") String cnh,
        @Null(message = "nao e permitido alterar a especialidade do instrutor") String especialidade
) {
}

