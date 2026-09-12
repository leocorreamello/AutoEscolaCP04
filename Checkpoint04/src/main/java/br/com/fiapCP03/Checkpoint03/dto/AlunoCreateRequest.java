package br.com.fiapCP03.Checkpoint03.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlunoCreateRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String telefone,
        @NotBlank String cpf,
        @Valid @NotNull EnderecoRequest endereco
) {
}

