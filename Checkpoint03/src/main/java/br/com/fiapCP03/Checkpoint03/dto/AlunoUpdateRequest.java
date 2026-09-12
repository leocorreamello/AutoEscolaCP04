package br.com.fiapCP03.Checkpoint03.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record AlunoUpdateRequest(
        @NotBlank String nome,
        @NotBlank String telefone,
        @Valid @NotNull EnderecoRequest endereco,
        @Null(message = "nao e permitido alterar o e-mail do aluno") String email,
        @Null(message = "nao e permitido alterar o CPF do aluno") String cpf
) {
}

