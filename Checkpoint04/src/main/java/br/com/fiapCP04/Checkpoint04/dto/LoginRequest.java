package br.com.fiapCP04.Checkpoint04.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String login,
        @NotBlank String senha
) {
}
