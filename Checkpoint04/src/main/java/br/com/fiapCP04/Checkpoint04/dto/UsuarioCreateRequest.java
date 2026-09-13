package br.com.fiapCP04.Checkpoint04.dto;

import br.com.fiapCP04.Checkpoint04.domain.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank @Size(max = 100) String login,
        @NotBlank @Size(min = 6, max = 100) String senha,
        @NotNull Perfil perfil
) {
}
