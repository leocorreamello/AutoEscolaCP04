package br.com.fiapCP04.Checkpoint04.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(
        @NotBlank String senhaAtual,
        @NotBlank @Size(min = 6, max = 100) String novaSenha
) {
}
