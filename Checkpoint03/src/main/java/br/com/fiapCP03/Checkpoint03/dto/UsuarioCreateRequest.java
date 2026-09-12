package br.com.fiapCP03.Checkpoint03.dto;

import br.com.fiapCP03.Checkpoint03.domain.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank @Size(max = 100) String login,
        @NotBlank @Size(min = 6, max = 100) String senha,
        @NotNull Perfil perfil
) {
}
