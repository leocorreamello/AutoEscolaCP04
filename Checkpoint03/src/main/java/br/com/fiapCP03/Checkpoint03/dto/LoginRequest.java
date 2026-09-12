package br.com.fiapCP03.Checkpoint03.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String login,
        @NotBlank String senha
) {
}
